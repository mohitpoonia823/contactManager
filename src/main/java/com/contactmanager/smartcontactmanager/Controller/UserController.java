package com.contactmanager.smartcontactmanager.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.contactmanager.smartcontactmanager.Entities.Contact;
import com.contactmanager.smartcontactmanager.Entities.User;
import com.contactmanager.smartcontactmanager.Helper.Message;
import com.contactmanager.smartcontactmanager.Repository.ContactRepository;
import com.contactmanager.smartcontactmanager.Repository.UserRepository;

import org.hibernate.type.StringNVarcharType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    // method for adding common data to response
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.getUserByUserName(username);
        model.addAttribute("user", user);
    }

    // dashboard home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "user/user_dashboard";
    }

    // open add contact handler

    @GetMapping("/add-contact")
    private String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "user/add_contact";
    }

    // processing add contact form
    @PostMapping("/process-contact")
    public String saveFormDetails(@ModelAttribute Contact contact,
            @RequestParam("profileimage") MultipartFile multipartFile, Principal principal, HttpSession session) {
        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            contact.setUser(user);

            // processing and uploading file
            if (multipartFile.isEmpty()) {
                contact.setImage("contact.png");

            } else {
                contact.setImage(multipartFile.getOriginalFilename());
                File file = new ClassPathResource("static/image").getFile();

                Path path = Paths.get(file.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());

                Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("image is uploaded");
            }

            user.getContact().add(contact);

            this.userRepository.save(user);

            session.setAttribute("message", new Message("Your contact is added !! Add more..", "success"));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error" + e.getMessage());
            session.setAttribute("message", new Message("Something went wrong, try again !!", "danger"));

        }
        return "redirect:/user/show-contact/0";
    }

    @GetMapping("/show-contact/{page}")
    public String showContact(@PathVariable("page")Integer page,Model model, Principal principal) {
        model.addAttribute("title", "Show user contacts");
        String userName = principal.getName();

        User user = this.userRepository.getUserByUserName(userName);

       Pageable pageable = PageRequest.of(page, 3);

        Page<Contact> contacts = this.contactRepository.findContactByUser(user.getuId(),pageable);
       

        model.addAttribute("Contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages",contacts.getTotalPages());
        System.out.println(contacts);

        return "user/show_contact";
    }


    //showing particular details
@RequestMapping("/{cid}/contact")
    public String showContactDetails(@PathVariable("cid") Integer cid,Model model,Principal principal){
        Optional<Contact> contactoOptional = this.contactRepository.findById(cid);
        Contact contact = contactoOptional.get();
       String username = principal.getName();
        User user =  this.userRepository.getUserByUserName(username);
        if(user.getuId() == contact.getUser().getuId()){
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getCname());
        }
       
        return "user/contact_details";
    }


    //delete contact handler
    @GetMapping("/delete/{cid}")
    public String deleteCOntact(@PathVariable("cid") Integer cid,Model model,Principal principal,HttpSession session){
       Optional<Contact> cOptional =   this.contactRepository.findById(cid);
       Contact contact =  cOptional.get();
       String username = principal.getName();
       User user =  this.userRepository.getUserByUserName(username);
       if(user.getuId() == contact.getUser().getuId()){
        this.contactRepository.delete(contact);
        session.setAttribute("message", new Message("Contact deleted successfully", "success"));
       }
        return "redirect:/user/show-contact/0";
    }


    //opn update form
    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid, Model model){
        model.addAttribute("title", "Update Contact Details");
        Contact contact = this.contactRepository.findById(cid).get();
        model.addAttribute("contact", contact);
        return "user/update-form";
    }

    //update form handler
           @PostMapping("/process-update")
    public String UpdateHandler(@ModelAttribute Contact contact,  @RequestParam("profileimage") MultipartFile multipartFile, Model model, HttpSession session,Principal principal){
        try {
           Contact oldContactDetail=  this.contactRepository.findById(contact.getCid()).get();
            if(!multipartFile.isEmpty()){
          //delte old 
          File deletefile = new ClassPathResource("static/image").getFile();
             File file1 = new File(deletefile, oldContactDetail.getImage());
             file1.delete();

          //update new pic
          File file = new ClassPathResource("static/image").getFile();

          Path path = Paths.get(file.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());

          Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            contact.setImage(multipartFile.getOriginalFilename());
            }else{
                contact.setImage(oldContactDetail.getImage());
            }
          User user = this.userRepository.getUserByUserName(principal.getName());
          contact.setUser(user);
            this.contactRepository.save(contact);
            session.setAttribute("meassage", new Message("Update Successfully", "success"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/user/"+contact.getCid()+"/contact";
    }

    //your profile
    @GetMapping("/profile")
    public String yourProfile(Model model){
     model.addAttribute("title", "Your Profile");
        return "/user/profile";
    }
}
