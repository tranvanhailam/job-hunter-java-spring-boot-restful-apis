package vn.kyler.job_hunter.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.kyler.job_hunter.domain.Subscriber;
import vn.kyler.job_hunter.domain.response.ResSubscriber;
import vn.kyler.job_hunter.domain.response.RestResponse;
import vn.kyler.job_hunter.service.SubscriberService;
import vn.kyler.job_hunter.service.exception.ExistsException;
import vn.kyler.job_hunter.service.exception.NotFoundException;

@Controller
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    public ResponseEntity<ResSubscriber> createSubscribe(@Valid @RequestBody Subscriber subscriber) throws ExistsException {
        Subscriber subscriberCreated = this.subscriberService.handleCreateSubscriber(subscriber);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.handleConvertToSubscriberDTO(subscriberCreated));
    }

    @PutMapping("/subscribers")
    public ResponseEntity<ResSubscriber> updateSubscribe(@Valid @RequestBody Subscriber subscriber) throws ExistsException, NotFoundException {
        Subscriber subscriberUpdated= this.subscriberService.handleUpdateSubscriber(subscriber);
        return ResponseEntity.status(HttpStatus.OK).body(this.subscriberService.handleConvertToSubscriberDTO(subscriberUpdated));
    }

    @DeleteMapping("/subscribers/{id}")
    public ResponseEntity<?> deleteSubscribe(@PathVariable Long id) throws NotFoundException {
        this.subscriberService.handleDeleteSubscriber(id);
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.OK.value());
        restResponse.setMessage("Delete subscriber successfully!");
        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }
}
