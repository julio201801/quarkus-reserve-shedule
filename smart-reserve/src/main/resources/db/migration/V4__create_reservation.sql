CREATE TABLE reservation (
         id UUID PRIMARY KEY,
         date DATE NOT NULL,
         start_time TIME NOT NULL,
         end_time TIME NOT NULL,
         customer_id UUID NOT NULL,
         profesional_id UUID NOT NULL,
         status VARCHAR(50) NOT NULL,
         created_at TIMESTAMP(6) NOT NULL DEFAULT now(),
         updated_at TIMESTAMP(6),
         CONSTRAINT fk_reservation_customer
             FOREIGN KEY (customer_id)
             REFERENCES customer(id),
         CONSTRAINT fk_reservation_professional
             FOREIGN KEY (profesional_id)
                 REFERENCES professional(id)
);
