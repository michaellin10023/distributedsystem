package com.michael.customer;

import com.michael.clients.fraud.FraudCheckResponse;
import com.michael.clients.fraud.FraudClient;
import com.michael.clients.notification.NotificationClient;
import com.michael.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;
    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .build();

        // todo: check if email valid
        // todo: check if email not taken
        customerRepository.save(customer);

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }

        // todo: make it async, i.e add to queue
        notificationClient.sendNotification(
            new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, welcome...", customer.getFirstName())
            )
        );
    }
}
