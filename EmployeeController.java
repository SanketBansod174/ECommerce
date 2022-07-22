package com.app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.custom_exceptions.ResourceNotFoundException;
import com.app.dto.EmployeeDTO;
import com.app.dto.ResponseMessage;
import com.app.service.IEmployeeService;
import com.app.service.ImageHandlingService;

@RestController // MANDATORY : composed of @Controller at the cls level + @ResponseBody(for
				// marshalling : java ---> json) addedd implicitly on ret types of all req
				// handling methods , annotated by @ReqMapping / @GetMapping .......
@RequestMapping("/api/employees")
@CrossOrigin // (origins = "http://localhost:3000")
@Validated
public class EmployeeController {
	// dep : emp service i/f
	@Autowired
	private IEmployeeService empService;

	// dep : image handling service i/f
	@Autowired
	private ImageHandlingService imageHandlingService;

	public EmployeeController() {
		System.out.println("in ctor of " + getClass());
	}

	// add req handling method (REST API call) to send all emps
	@GetMapping
	public ResponseEntity<?> listAllEmps() {
		System.out.println("in list emps");
		List<EmployeeDTO> list = empService.getAllEmpDetails();
		// o.s.ResponseEntity(T body,HttpStatus sts)
//		if (list.isEmpty()) 
//			return new ResponseEntity<>("Empty Emp List !!!!", HttpStatus.OK);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	// add req handling method to create new emp
	@PostMapping
	public ResponseEntity<EmployeeDTO> saveEmpDetails(@RequestBody @Valid EmployeeDTO emp)
	// To inform SC , to un marshall(de-serialization , json/xml --> Java obj) the
	// method arg.
	{
		System.out.println("in save emp " + emp);// id : null...
//		return  ResponseEntity.ok(empService.saveEmpDetails(emp));

		return new ResponseEntity<>(empService.saveEmpDetails(emp), HttpStatus.CREATED);
	}

	// add req handling method to delete emp details
	@DeleteMapping("/{empId}") // can use ANY name for a path var.
	// @PathVariable => a binding between a path var to method arg.
	public String deleteEmpDetails(@PathVariable @Range(min = 1, max = 100, message = "Invalid emp id!!!") int empId) {
		System.out.println("in del emp " + empId);
		return empService.deleteEmpDetails(empId);
	}

	// add a method to get specific emp dtls
	@GetMapping("/{id}")
	// @PathVariable => a binding between a path var to method arg.
	public ResponseEntity<?> getEmpDetails(@PathVariable int id) {
		System.out.println("in get emp " + id);
		EmployeeDTO employee = empService.getEmpDetails(id);
		System.out.println("emp class " + employee.getClass());
		return ResponseEntity.ok(employee);

	}

	// add a method to update existing resource
	@PutMapping
	public ResponseEntity<?> updateEmpDetails(@RequestBody EmployeeDTO emp) {
		System.out.println("in update emp " + emp);// id not null
//		return empService.updateEmpDetails(emp);
		return ResponseEntity.ok(empService.updateEmpDetails(emp));
	}

	// add a method to upload image on the server side folder
	@PostMapping("/{empId}/image")
	public ResponseEntity<?> uploadImage(@PathVariable int empId, @RequestParam MultipartFile imageFile)
			throws IOException {

		System.out.println("-------------------------------------");
		System.out.println("in upload image " + empId);
		System.out.println("uploaded img file name " + imageFile.getOriginalFilename() + " content type "
				+ imageFile.getContentType() + " size " + imageFile.getSize()); // invoke
		// service layer method to save uploaded file in the server side folder //
		// --ImageHandligService
		EmployeeDTO employeeDTO = imageHandlingService.storeImage(empId, imageFile);
		return ResponseEntity.ok(employeeDTO);

	}

	@PostMapping("/{empId}/images")
	public ResponseEntity<?> uploadMultipleImages(@PathVariable int empId, @RequestParam MultipartFile[] imageFile) {
		System.out.println("  ----------Inside ---------   Length " + imageFile.length);
		String message = "";
		try {
			System.out.println("---------inside try block-------------");
			List<String> fileNames = new ArrayList<>();
			Arrays.asList(imageFile).stream().forEach(file -> {

				try {
					imageHandlingService.storeMultipleImages(empId, file);
				} catch (IOException e) {
					e.printStackTrace();
				}

				fileNames.add(file.getOriginalFilename());
			});
			message = "Uploaded the files successfully: " + fileNames;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
	}

	// add req handling method to download image for specific emp
	@GetMapping(value = "/{empId}/image", produces = { MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE,
			MediaType.IMAGE_PNG_VALUE })
	public ResponseEntity<?> downloadImage(@PathVariable int empId) throws IOException {
		System.out.println("in img download " + empId);
		// invoke service layer method , to get image data from the server side folder
		byte[] imageContents = imageHandlingService.restoreImage(empId);
		return ResponseEntity.ok(imageContents);
	}

	@GetMapping("/greatersalary/{sal}")
	public ResponseEntity<?> getEmpGreaterSalary(@PathVariable double sal) {
		List<EmployeeDTO> list = empService.getEmpByGreaterSalary(sal);
		list.forEach(System.out::println);

		if (!list.isEmpty())
			return ResponseEntity.ok(list);
		else
			throw new ResourceNotFoundException("No Employees Found");
	}

	@GetMapping("/get/{dept}/{loc}")
	public ResponseEntity<?> getEmpDeptAndLocation(@PathVariable String dept, @PathVariable String loc) {
		System.out.println("dept : " + dept + "  loc : " + loc);
		List<EmployeeDTO> list = empService.getEmpsByDepartmentAndWorkLocation(dept, loc);
		if (!list.isEmpty())
			return ResponseEntity.ok(list);
		else
			throw new ResourceNotFoundException("No Employees Found");
	}

	@PutMapping("/update/{dept}/{salInc}")
	public int updateEmpSalary(@PathVariable String dept, @PathVariable double salInc) {
		return empService.updateEmpSalaryByDepartment(salInc, dept);
	}

}
