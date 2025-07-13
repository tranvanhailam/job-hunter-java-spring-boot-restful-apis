package vn.kyler.job_hunter.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.kyler.job_hunter.domain.Subscriber;
import vn.kyler.job_hunter.domain.response.ResJobDTO;
import vn.kyler.job_hunter.domain.response.ResSubscriber;
import vn.kyler.job_hunter.repository.SubscriberRepository;
import vn.kyler.job_hunter.service.exception.ExistsException;
import vn.kyler.job_hunter.service.exception.NotFoundException;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillService skillService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillService skillService) {
        this.subscriberRepository = subscriberRepository;
        this.skillService = skillService;
    }


    public Subscriber handleCreateSubscriber(Subscriber subscriber) throws ExistsException {
        boolean existsEmail = this.subscriberRepository.existsByEmail(subscriber.getEmail());
        if (existsEmail) {
            throw new ExistsException("Emai " + subscriber.getEmail() + " already exists");
        }
        if (subscriber.getSkills() != null) {
            subscriber.setSkills(
                    subscriber.getSkills().stream()
                            .map(skill -> {
                                try {
                                    return this.skillService.handleGetSkillById(skill.getId());
                                } catch (NotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            }).collect(Collectors.toList())
            );
        }
        return subscriberRepository.save(subscriber);
    }

    public Subscriber handleUpdateSubscriber(Subscriber subscriber) throws NotFoundException, ExistsException {
        Optional<Subscriber> optionalSubscriber = this.subscriberRepository.findById(subscriber.getId());
        if (!optionalSubscriber.isPresent()) {
            throw new NotFoundException("Subscriber with id " + subscriber.getId() + " not found");
        }
//        boolean existsEmail = this.subscriberRepository.existsByEmail(subscriber.getEmail());
//        if (existsEmail) {
//            throw new ExistsException("Emai " + subscriber.getEmail() + " already exists");
//        }
        Subscriber subscriberToUpdate = optionalSubscriber.get();
//        subscriberToUpdate.setEmail(subscriber.getEmail());
        subscriberToUpdate.setName(subscriber.getName());
        if (subscriber.getSkills() != null) {
            subscriberToUpdate.setSkills(
                    subscriber.getSkills().stream().map(skill -> {
                        try {
                            return this.skillService.handleGetSkillById(skill.getId());
                        } catch (NotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).collect(Collectors.toList())
            );
        }
        return subscriberRepository.save(subscriberToUpdate);
    }

    public void handleDeleteSubscriber(long id) throws NotFoundException {
        Optional<Subscriber> optionalSubscriber = this.subscriberRepository.findById(id);
        if (!optionalSubscriber.isPresent()) {
            throw new NotFoundException("Subscriber with id " + id + " not found");
        }
        this.subscriberRepository.delete(optionalSubscriber.get());
    }

    public ResSubscriber handleConvertToSubscriberDTO(Subscriber subscriber) {
        ResSubscriber subscriberDTO = new ResSubscriber();
        subscriberDTO.setId(subscriber.getId());
        subscriberDTO.setName(subscriber.getName());
        subscriberDTO.setEmail(subscriber.getEmail());
        subscriberDTO.setCreatedAt(subscriber.getCreatedAt());
        subscriberDTO.setUpdatedAt(subscriber.getUpdatedAt());
        subscriberDTO.setCreatedBy(subscriber.getCreatedBy());
        subscriberDTO.setUpdatedBy(subscriber.getUpdatedBy());
        if (subscriber.getSkills() != null) {
            subscriberDTO.setSkills(
                    subscriber.getSkills().stream()
                            .map(skill -> {
                                ResSubscriber.Skill skillDTO = new ResSubscriber.Skill();
                                skillDTO.setId(skill.getId());
                                skillDTO.setName(skill.getName());
                                return skillDTO;
                            }).collect(Collectors.toList())
            );
        }
        return subscriberDTO;
    }


}
