package lv.ctco.student;

import lv.ctco.Assignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired //same as Inject
    StudentRepository studentRepository;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addStudent(@RequestBody Student student, UriComponentsBuilder b) {
        studentRepository.save(student);

        UriComponents uriComponents =
                b.path("/students/{id}").buildAndExpand(student.getId());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(uriComponents.toUri());
        return new ResponseEntity<String>(responseHeaders,HttpStatus.CREATED);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getStudentById(@PathVariable("id") long id) {

        if (studentRepository.exists(id)) {
            Student student = studentRepository.findOne(id);
            return new ResponseEntity<>(student, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Transactional
    @RequestMapping(path = " /{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateStudentByID(@PathVariable("id") long id,
                                               @RequestBody Student student) {

        if (studentRepository.exists(id)) {
            Student editedStudent = studentRepository.findOne(id);
            editedStudent.setName(student.getName());
            editedStudent.setSurname(student.getSurname());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteById(@PathVariable("id") long id) {
        if (studentRepository.exists(id)) {
            studentRepository.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(path = "/{id}/assignment", method = RequestMethod.POST)
    public ResponseEntity<?> addAssignment(@PathVariable("id") long id, @RequestBody Assignment assignment) {
        if (studentRepository.exists(id)) {
            Student student = studentRepository.findOne(id);
            student.addAssignment(assignment);
            studentRepository.save(student);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Transactional
    @RequestMapping(path = "/{id}/assignment/{aId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAssignmentById(@PathVariable("id") long id,
            @PathVariable("aId") long aId) {
        if (studentRepository.exists(id)) {
            Student student = studentRepository.findOne(id);
            if (student.removeAssignmentById(aId)){

                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Transactional
    @RequestMapping(path = "/{id}/assignment/{aId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateAssignmentById(@PathVariable("id") long id,
                                                  @PathVariable("aId") long aId, @RequestBody Assignment assignment) {
        assignment.setId(aId);
        if (studentRepository.exists(id)) {
            Student student = studentRepository.findOne(id);
            if (student.updateAssignment(assignment)){

                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



}
