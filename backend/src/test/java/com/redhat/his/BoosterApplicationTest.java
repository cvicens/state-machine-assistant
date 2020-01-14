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

package com.redhat.his;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertFalse;

import java.util.Collections;

import com.redhat.his.service.Patient;
import com.redhat.his.service.PatientRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BoosterApplicationTest {

    private static final String PATIENTS_PATH = "api/patients";

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private PatientRepository patientRepository;

    @Before
    public void beforeTest() {
        patientRepository.deleteAll();
        RestAssured.baseURI = String.format("http://localhost:%d/" + PATIENTS_PATH, port);
    }

    @Test
    public void testGetAll() {
        Patient john = patientRepository.save(new Patient("0123456789Z", "JOHN", "SMITH", "idle"));
        Patient peter = patientRepository.save(new Patient("9876543210W", "PETER", "JONES", "idle"));
        requestSpecification()
                .get()
                .then()
                .statusCode(200)
                .body("patientId", hasItems(john.getPatientId(), peter.getPatientId()))
                .body("personalId", hasItems(john.getPersonalId(), peter.getPersonalId()))
                .body("firstName", hasItems(john.getFirstName(), peter.getFirstName()))
                .body("lastName", hasItems(john.getLastName(), peter.getLastName()))
                .body("stage", hasItems(john.getStage(), peter.getStage()));
    }

    @Test
    public void testGetEmptyArray() {
        requestSpecification()
                .get()
                .then()
                .statusCode(200)
                .body(is("[]"));
    }

    @Test
    public void testGetOne() {
        Patient john = patientRepository.save(new Patient("0123456789Z", "JOHN", "SMITH", "idle"));
        requestSpecification()
                .get(String.valueOf(john.getPatientId()))
                .then()
                .statusCode(200)
                .body("patientId", is(john.getPatientId()))
                .body("personalId", is(john.getPersonalId()));
    }

    @Test
    public void testGetNotExisting() {
        requestSpecification()
                .get("0")
                .then()
                .statusCode(404);
    }

    @Test
    public void testPost() {
        Patient john = patientRepository.save(new Patient("0123456789Z", "JOHN", "SMITH", "idle"));
        requestSpecification()
                .contentType(ContentType.JSON)
                .body(john)
                .post()
                .then()
                .statusCode(201)
                .body("patientId", not(isEmptyString()))
                .body("personalId", is("0123456789Z"));
    }

    @Test
    public void testPostWithNonJsonPayload() {
        requestSpecification()
                .contentType(ContentType.XML)
                .when()
                .post()
                .then()
                .statusCode(415);
    }

    @Test
    public void testPostWithEmptyPayload() {
        requestSpecification()
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(415);
    }

    @Test
    public void testPut() {
        Patient john = patientRepository.save(new Patient("0123456789Z", "JOHN", "SMITH", "idle"));
        requestSpecification()
                .contentType(ContentType.JSON)
                .body(Collections.singletonMap("firstName", "DAVID"))
                .when()
                .put(String.valueOf(john.getPatientId()))
                .then()
                .statusCode(200)
                .body("patientId", is(john.getPatientId()))
                .body("firstName", is("DAVID"));

    }

    @Test
    public void testPutNotExisting() {
        requestSpecification()
                .contentType(ContentType.JSON)
                .body(Collections.singletonMap("personalId", "99999999Z"))
                .when()
                .put("/0")
                .then()
                .statusCode(404);
    }

    @Test
    public void testPutWithNonJsonPayload() {
        Patient john = patientRepository.save(new Patient("0123456789Z", "JOHN", "SMITH", "idle"));
        requestSpecification()
                .contentType(ContentType.XML)
                .when()
                .put(String.valueOf(john.getPatientId()))
                .then()
                .statusCode(415);
    }

    @Test
    public void testPutWithEmptyPayload() {
        Patient john = patientRepository.save(new Patient("0123456789Z", "JOHN", "SMITH", "idle"));
        requestSpecification()
                .contentType(ContentType.JSON)
                .when()
                .put(String.valueOf(john.getPatientId()))
                .then()
                .statusCode(415);
    }

    @Test
    public void testDelete() {
        Patient john = patientRepository.save(new Patient("0123456789Z", "JOHN", "SMITH", "idle"));
        requestSpecification()
                .delete(String.valueOf(john.getPatientId()))
                .then()
                .statusCode(204);
        assertFalse(patientRepository.existsById(john.getPatientId()));
    }

    @Test
    public void testDeleteNotExisting() {
        requestSpecification()
                .delete("/0")
                .then()
                .statusCode(404);
    }


    private RequestSpecification requestSpecification() {
        return given().baseUri(String.format("http://localhost:%d/%s", port, PATIENTS_PATH));
    }
}
