package com.thethreeamigosmakingsense.covidoperationscentralsystem.Controller;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Model.*;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.BookingService;
import com.thethreeamigosmakingsense.covidoperationscentralsystem.Service.UserService;
import groovy.lang.Tuple2;
import groovy.lang.Tuple3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private HttpServletRequest http;

    /**
     * GetMapping for displaying information on individual users. For staff usage only
     * @param username of user
     * @param model model
     * @return html
     */
    @GetMapping(value = "/user/{username}")
    private String readUserInfo(@PathVariable("username") String username, Model model) {

        // Simple users are not allowed here
        if (userService.checkPrivilege(http.getRemoteUser()).equals("ROLE_USER"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);


        User user = userService.fetchUser(username); // fetch from database
        Authority authority = userService.fetchAuthority(user); // fetch from database

        /*
        List of Tuple3s containing all bookings, their BookingType (TestResult or Vaccine object) and a boolean
        which is true when a booking is cancellable.
         */
        List<Tuple3<Booking, BookingType, Boolean>> bookingList = bookingService.fetchUsersBoookings(username);

        Tuple2<Boolean, Boolean> hasBookings = userHasBooking(bookingList); // booleans true if user has test or vaccine booking respectively

        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND); // if username doesn't exist

        // Strings the the dropdowns where the staff can edit booking status or roles of user.
        String[] vaccineStatus = {"PENDING", "RECEIVED", "CANCELLED"};
        String[] testStatus = {"TEST_PENDING", "RESULT_PENDING", "NEGATIVE", "POSITIVE", "CANCELLED"};
        String[] authorities = {"ROLE_USER", "ROLE_PERSONNEL", "ROLE_ADMIN"};

        model.addAttribute("isRemoteUser", username.equals(http.getRemoteUser())); // true if page belongs to the user viewing it (hides certain options)
        model.addAttribute("vaccineStatus", vaccineStatus); // dropdown
        model.addAttribute("testStatus", testStatus); // dropdown
        model.addAttribute("hasTestBooking", hasBookings.getFirst()); // displays test table if true
        model.addAttribute("hasVaccineBooking", hasBookings.getSecond()); // displays vaccine table if true
        model.addAttribute("user", user); // object
        model.addAttribute("authority", authority); // object
        model.addAttribute("authorityStrings", authorities); // dropdown
        model.addAttribute("bookings", bookingList); // list

        return "profile/user";
    }

    /**
     * Method to update status of TestResult
     * @param username of user
     * @param model model
     * @param id booking_id
     * @param status new status
     * @return html
     */
    @PostMapping(value = "/user/{username}", params = "updateTest")
    private String updateTest(@PathVariable("username") String username, Model model, String id,
                              @RequestParam(value = "updateTest") String status) {

        if (status.equalsIgnoreCase("cancel")) status = "CANCELLED";

        TestResult testResult = new TestResult(Integer.parseInt(id), status);

        bookingService.updateStatus(testResult); // save update to database

        return readUserInfo(username, model); // refresh page
    }

    /**
     * Method to update status of Vaccine
     * @param username of user
     * @param model model
     * @param id booking_id
     * @param status new status
     * @param type of vaccine (first or second shot)
     * @return html
     */
    @PostMapping(value = "/user/{username}", params = "updateVaccine")
    private String updateVaccine(@PathVariable("username") String username, Model model, String id,
                                 @RequestParam(value = "updateVaccine") String status, String type) {

        if (status.equalsIgnoreCase("cancel")) status = "CANCELLED";

        Vaccine vaccine = new Vaccine(Integer.parseInt(id), status, type);

        bookingService.updateStatus(vaccine); // save to database

        /*
        status of FIRST_SHOT is being changed to received, a time is
        automatically booked for the second shot 24 days later
         */
        if (vaccine.getType().equals("FIRST_SHOT") && vaccine.getStatus().equals("RECEIVED"))
            bookingService.autoBookSecondShot(username, vaccine);


        return readUserInfo(username, model);
    }

    /**
     * Method for updating a user's role (only admins can do this)
     * @param username of user
     * @param model model
     * @param authority new role
     * @return html
     */
    @PostMapping(value = "/user/{username}", params = "updateAuthority")
    private String updateAuthority(@PathVariable("username") String username, Model model,
                                   @RequestParam(value="updateAuthority") String authority) {

        switch (authority) {
            case "USER" -> authority = "ROLE_USER";
            case "PERSONNEL" -> authority = "ROLE_PERSONNEL";
            case "ADMIN" -> authority = "ROLE_ADMIN";
        }

        Authority auth = new Authority();
        auth.setUsername(username);
        auth.setAuthority(authority);

        userService.updateAuthority(auth); // save to database

        return readUserInfo(username, model);
    }

    /**
     * Method to disable or enable account
     * @param username of user
     * @param model model
     * @param changeEnabled state change
     * @return html
     */
    @PostMapping(value = "/user/{username}", params = "changeEnabled")
    private String changeEnabled(@PathVariable("username") String username, Model model,
                                 @RequestParam(value = "changeEnabled") String changeEnabled) {

        User user = userService.fetchUser(username);

        switch (changeEnabled) {
            case "DISABLE" -> user.setEnabled(false);
            case "ENABLE" -> user.setEnabled(true);
        }

        userService.changeEnabledUser(user); // save to database

        return readUserInfo(username, model);
    }

    /**
     * GetMapping to see one's own bookings
     * @param model model
     * @return html
     */
    @GetMapping(value = "/mybookings")
    private String myBookings(Model model){

        List<Tuple3<Booking, BookingType, Boolean>> bookingList = // list of user's bookings
                bookingService.fetchUsersBoookings(http.getRemoteUser()); // fetch from database

        filterBookings(bookingList); // removes all cancelled bookings from list

        Tuple2<Boolean, Boolean> hasBookings = userHasBooking(bookingList); // booleans true if user has test or vaccine booking respectively

        model.addAttribute("navItem", "mybookings");
        model.addAttribute("hasSecondShot", bookingService.userHasSecondShot(http.getRemoteUser())); // displays option to print certificate if true
        model.addAttribute("hasTestBooking", hasBookings.getFirst());     // if false the table is never rendered
        model.addAttribute("hasVaccineBooking", hasBookings.getSecond()); // if false the table is never rendered
        model.addAttribute("bookings", bookingList);
        model.addAttribute("user", new User()); // is never used, prevents page fragment from throwing error
        return "profile/myBookings";
    }

    /**
     * PostMapping for user to cancel test booking
     * @param model model
     * @param id booking_id
     * @param status of TestResult
     * @return html
     */
    @PostMapping(value = "/mybookings", params = "updateTest")
    private String myBookings(Model model, String id,
                              @RequestParam(value = "updateTest") String status) {

        if (status.equalsIgnoreCase("cancel")) status = "CANCELLED";

        TestResult testResult = new TestResult(Integer.parseInt(id), status);

        bookingService.updateStatus(testResult); // save to database

        return myBookings(model);
    }

    /**
     * PostMapping for user to cancel vaccine booking
     * @param model model
     * @param id booking_id
     * @param status of Vaccine
     * @param type of Vaccine (first or second shot)
     * @return html
     */
    @PostMapping(value = "/mybookings", params = "updateVaccine")
    private String myBookings(Model model, String id,
                              @RequestParam(value = "updateVaccine") String status, String type) {

        if (status.equalsIgnoreCase("cancel")) status = "CANCELLED";

        Vaccine vaccine = new Vaccine(Integer.parseInt(id), status, type);

        bookingService.updateStatus(vaccine); // save to database

        return myBookings(model);
    }

    /**
     * GetMapping to generate PDF with Vaccine Certificate

     * Full disclosure: we don't understand most of what is going on in this method.
     * Content of method is inspired by/copied from articles:
     *      https://www.baeldung.com/thymeleaf-generate-pdf
     *      https://springhow.com/spring-boot-pdf-generation/

     * @return converted pdf from html
     */
    @GetMapping("/vaccine_certificate")
    private ResponseEntity<byte[]> parseTemplate() {

        // users that have not been vaccinated twice are not allowed here
        if (!bookingService.userHasSecondShot(http.getRemoteUser()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        List<Tuple3<Booking, BookingType, Boolean>> bookingList = // List of all bookings by user
                bookingService.fetchUsersBoookings(http.getRemoteUser());
        bookingList.removeIf(booking -> !booking.getFirst().getType().equals("VACCINE")); // filters out all bookings not of type VACCINE
        bookingList.removeIf(bookingType -> !bookingType.getSecond().getStatus().equals("RECEIVED")); // filter out all bookings without status RECEIVED

        Context context = new Context();
        context.setVariable("user", userService.fetchUser(http.getRemoteUser()));
        context.setVariable("bookings", bookingList);

        String html = templateEngine.process("templates/certificate/vaccine_certificate.html", context);

        ByteArrayOutputStream target = new ByteArrayOutputStream();

        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://localhost:6060");

        HtmlConverter.convertToPdf(html, target, converterProperties);

        byte[] bytes = target.toByteArray();

        // return downloadable PDF
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

    /**
     * Returns a Tuple2 object with two Booleans.
     *
     * The first Boolean is true if the user has any bookings of type TEST.
     * The second Boolean is true is the user ahas any bookings of type VACCINE.
     *
     * @param bookingList list of a specific user's bookings
     * @return Tuple2<Boolean, Boolean>
     */
    private Tuple2<Boolean, Boolean> userHasBooking(List<Tuple3<Booking, BookingType, Boolean>> bookingList) {

        boolean hasTestBooking = false;
        boolean hasVaccineBooking = false;

        for (Tuple3<Booking, BookingType, Boolean> booking : bookingList ) {
            if (!hasTestBooking)
                if (booking.getFirst().getType().equals("TEST")) hasTestBooking = true;

            if (!hasVaccineBooking)
                if (booking.getFirst().getType().equals("VACCINE")) hasVaccineBooking = true;

            if (hasTestBooking && hasVaccineBooking)
                break;
        }

        return new Tuple2<>(hasTestBooking, hasVaccineBooking);
    }

    /**
     * Method that takes a list of bookings and filters out all that are cancelled
     * @param bookingList a list of bookings
     */
    private void filterBookings(List<Tuple3<Booking, BookingType, Boolean>> bookingList) {
        bookingList.removeIf(booking -> booking.getSecond().getStatus().equals("CANCELLED"));
    }
}
