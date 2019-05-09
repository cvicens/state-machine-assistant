/*
 * Copyright 2016-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.his.service;

import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.redhat.his.exception.NotFoundException;
import com.redhat.his.exception.UnprocessableEntityException;
import com.redhat.his.exception.UnsupportedMediaTypeException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/patients")
public class PatientController {

    private final PatientRepository repository;

    public PatientController(PatientRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public Patient get(@PathVariable("id") Integer id) {
        verifyPatientExists(id);

        return repository.findById(id).get();
    }

    @GetMapping
    public List<Patient> getAll() {
        Spliterator<Patient> patients = repository.findAll()
                .spliterator();

        return StreamSupport
                .stream(patients, false)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Patient post(@RequestBody(required = false) Patient patient) {
        verifyCorrectPayload(patient);

        return repository.save(patient);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public Patient put(@PathVariable("id") Integer id, @RequestBody(required = false) Patient patient) {
        verifyPatientExists(id);
        verifyCorrectPayload(patient);

        patient.setId(id);
        return repository.save(patient);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        verifyPatientExists(id);

        repository.deleteById(id);
    }

    private void verifyPatientExists(Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(String.format("Patient with id=%d was not found", id));
        }
    }

    private void verifyCorrectPayload(Patient patient) {
        if (Objects.isNull(patient)) {
            throw new UnsupportedMediaTypeException("Patient cannot be null");
        }

        if (!Objects.isNull(patient.getPatientId())) {
            throw new UnprocessableEntityException("Id field must be generated");
        }
    }

}
