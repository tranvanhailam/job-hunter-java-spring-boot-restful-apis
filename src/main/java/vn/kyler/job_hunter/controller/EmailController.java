package vn.kyler.job_hunter.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.kyler.job_hunter.domain.Job;
import vn.kyler.job_hunter.domain.Skill;
import vn.kyler.job_hunter.domain.Subscriber;
import vn.kyler.job_hunter.domain.response.RestResponse;
import vn.kyler.job_hunter.repository.JobRepository;
import vn.kyler.job_hunter.repository.SubscriberRepository;
import vn.kyler.job_hunter.service.EmailService;

import java.util.List;

@RestController
public class EmailController {

    private final EmailService emailService;
    private final SubscriberRepository subscriberRepository;
    private final JobRepository jobRepository;

    public EmailController(EmailService emailService, SubscriberRepository subscriberRepository, JobRepository jobRepository) {
        this.emailService = emailService;
        this.subscriberRepository = subscriberRepository;
        this.jobRepository = jobRepository;
    }

    @PostMapping("/email/{emailName}")
    public ResponseEntity<?> sendEmail(@PathVariable("emailName") String emailName) {
        String content = "Hãy chuyển cho tôi 500 triệu\n" +
                "<img src=\"https://scontent.fhan18-1.fna.fbcdn.net/v/t39.30808-1/475764953_1823505461795961_7343318100894508359_n.jpg?stp=dst-jpg_s200x200_tt6&_nc_cat=105&ccb=1-7&_nc_sid=e99d92&_nc_eui2=AeFEBgDhdXWndKK8K-coi2kn0q_oQAuG1i3Sr-hAC4bWLaNTsFYPr09KzF7JwIULHmntWUhCaqI-GMgs0Nt5arML&_nc_ohc=FkiPcxhMSkAQ7kNvwHgCoE7&_nc_oc=AdmY2g09evaKU4gj9qXyXlYmWbWo228OfAEr9SCxYRwyVF-_Jtyj7BkAUaKdwnoztmuVvDJG2RCPtwBef4wYStzy&_nc_zt=24&_nc_ht=scontent.fhan18-1.fna&_nc_gid=qyIvj8HNqQmRKRh3p8kmbw&oh=00_AfTTScW83Y3_eAOTcYvUI46SoCq5pe7fGc5F9ZyhceuHnw&oe=68783A10\">\n";
        this.emailService.handleSendEmailSync(emailName, "HÃY CHUYỂN 500 TRIỆU", content, false, true);
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.OK.value());
        restResponse.setMessage("Email send to " + emailName + " successfully!");
        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }

    @PostMapping("/email")
    @Scheduled(cron = "0 0 8 ? * SAT")
    @Transactional
    public ResponseEntity<?> sendEmail() {
        List<Subscriber> subscribers = this.subscriberRepository.findAll();
        if (subscribers != null && subscribers.size() > 0) {
            for (Subscriber subscriber : subscribers) {
                List<Skill> skills = subscriber.getSkills();
                if (skills != null && skills.size() > 0) {
                    List<Job> jobs = this.jobRepository.findBySkillsIn(skills);
                    if (jobs != null && jobs.size() > 0) {
                        this.emailService.handleSendEmailFromTemplateSync(subscriber.getEmail(), "New Job Matches Just for You!", "job", subscriber.getName(), jobs);
                    }
                }
            }
        }
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.OK.value());
        restResponse.setMessage("Send email successfully!");
        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }
}
