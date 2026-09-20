CREATE TABLE available_schedule (
                                    id UUID PRIMARY KEY ,
                                    profesional_id UUID NOT NULL,
                                    date DATE NOT NULL,
                                    start_time TIME NOT NULL,
                                    end_time TIME,
                                    active_status BOOLEAN NOT NULL DEFAULT TRUE,
                                    CONSTRAINT fk_available_schedule_professional
                                        FOREIGN KEY (profesional_id)
                                            REFERENCES professional(id)
);