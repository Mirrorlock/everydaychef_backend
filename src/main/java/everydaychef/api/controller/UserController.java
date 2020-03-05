package everydaychef.api.controller;

import everydaychef.api.config.JwtToken;
import everydaychef.api.exceptions.ValidationException;
import everydaychef.api.model.Family;
import everydaychef.api.model.Recipe;
import everydaychef.api.model.User;
import everydaychef.api.repository.FamilyRepository;
import everydaychef.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserController {

    final private UserRepository userRepository;
    final private FamilyRepository familyRepository;
    private Logger logger = LoggerFactory.getLogger(UserController.class);


    public UserController(UserRepository userRepository,
                          FamilyRepository familyRepository) {
        this.userRepository = userRepository;
        this.familyRepository = familyRepository;
    }

    private Optional<User> getUserById(String Id){
        return userRepository.findById(Integer.parseInt(Id));
    }


    @GetMapping("/user")
    public ResponseEntity<List<User>> getUser() throws NoSuchElementException {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId) throws NoSuchElementException {
        int Id = Integer.parseInt(userId);
        Optional<User> foundUser = userRepository
                .findById(Id);
        return foundUser
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/name/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> foundUser = userRepository
                .findByName(username);
        System.out.println("User searched with name: "+ username + " and found: " + foundUser.orElse(null) );
        return foundUser
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}/family")
    public ResponseEntity<Family> getUserFamily(@PathVariable String userId){
        int userIdNum = Integer.parseInt(userId);
        return userRepository.findById(userIdNum)
                .map(user -> new ResponseEntity<>(user.getFamily(), HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}/invitations")
    public ResponseEntity<Set<Family>> getInvitations(@PathVariable String userId) {
        System.out.println("Received request for user invitations!");
        return getUserById(userId)
                .map(user -> new ResponseEntity<>(user.getInvitations(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{id}/recipes")
    public ResponseEntity<List<Recipe>> getCreatedRecipes(@PathVariable String id){
        return getUserById(id)
                .map(user -> ResponseEntity.ok().body(user.getRecipes()))
                .orElse(ResponseEntity.notFound().build());
    }

    






    @PostMapping("/user")
    public ResponseEntity<?> create(@RequestBody Map<String, String> body) /*throws ValidationException */{
        String username = body.get("username");
        if (userRepository.existsByName(username)){
            return ResponseEntity.badRequest().body("Username already exists!");
        }
        String password = body.get("password");
        String email = body.get("email");
        char authenticationMethod = body.get("authentication_method") != null
                        ? body.get("authentication_method").charAt(0) : 'l';
        if(password != null){
            password = new BCryptPasswordEncoder().encode(password);
        }
        User newUser = new User(username, email, password, null, authenticationMethod);
        newUser.setDefaultFamily(familyRepository);
        newUser = userRepository.save(newUser);
        return ResponseEntity.ok().body(newUser);
    }







    @PutMapping("/user/{userId}/family/{familyName}")
    public ResponseEntity</*Family*/?> changeFamily(@PathVariable String userId, @PathVariable String familyName){
        if(familyRepository.existsByName(familyName)){
            return ResponseEntity.badRequest().body("Family name already taken!");
        }
        Family newFamily = new Family(familyName);
        return getUserById(userId).map(user -> {
            user.setFamily(newFamily);
            newFamily.getUsers().add(user);
            familyRepository.save(newFamily);
            return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
        }).orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PutMapping("/user/{userId}/family/{familyId}/answer_invitation")
    public ResponseEntity</*Family*/?> answerInvitation(@PathVariable String userId, @PathVariable String familyId,
                                                    @RequestBody Map<String, Boolean> body){
        Family family = familyRepository.findById(Integer.parseInt(familyId)).orElse(null);
        if(family == null){
            return ResponseEntity.notFound().build();
        }

        return getUserById(userId).map(user -> {
            if(body.get("isAccepted")){
                user.setFamily(family);
                family.getUsers().add(user);

            }
            user.getInvitations().remove(family);
            family.getInvitedUsers().remove(user);
            familyRepository.save(family);
            return ResponseEntity.ok().body(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

//    @PutMapping("/user/{userId}/family/{familyName}/reject")
//    public ResponseEntity<Family> rejectInvitation(@PathVariable String userId, @PathVariable String familyId) {
//        Family family = familyRepository.findById(Integer.parseInt(familyId)).orElse(null);
//        if(family == null){
//            return ResponseEntity.notFound().build();
//        }
//
//        return getUserById(userId).map(user -> {
//            user.setFamily(family);
//            family.getUsers().add(user);
//            familyRepository.save(family);
//            userRepository.save(user);
//            return new ResponseEntity<>(family, HttpStatus.OK);
//        }).orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
//    }

        @PutMapping("/user/{userId}/family/{familyId}/invite")
    public ResponseEntity<Boolean> inviteUserToFamily(@PathVariable String userId, @PathVariable String familyId,
                                    @RequestBody Map<String, String> body){
        Optional<Family> optionalFamily =  familyRepository.findById(Integer.parseInt(familyId));
        if(optionalFamily.isPresent()){
            Family family = optionalFamily.get();
            getUserById(userId).ifPresent(user -> {
                user.addInvitation(family);
                userRepository.save(user);
                family.getInvitedUsers().add(user);
            });
            familyRepository.save(family);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }else{
            System.out.println("We are here!");
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }






    @DeleteMapping("/user/{userId}/family")
    public ResponseEntity<Family> userLeaveFamily(@PathVariable String userId){
        Optional<User> userOpt = getUserById(userId);
        if(userOpt.isPresent()){
            User user = userOpt.get();
            user.setDefaultFamily(familyRepository);

            return new ResponseEntity<>(userRepository.save(user).getFamily(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
