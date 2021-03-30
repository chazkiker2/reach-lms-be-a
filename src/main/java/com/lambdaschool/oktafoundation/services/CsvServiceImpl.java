package com.lambdaschool.oktafoundation.services;


import com.lambdaschool.oktafoundation.models.*;
import com.lambdaschool.oktafoundation.utils.CsvHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public class CsvServiceImpl
		implements CsvService {

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private CourseService courseService;

	@Override
	public void save(MultipartFile file) {
		Role studentRole = roleService.findByName(RoleType.STUDENT.name());
		try {
			List<User> users = CsvHelper.csvToStudents(file);
			for (User user : users) {
				// we should check to see:
				// 1. if this user exists in Okta. (If not, create)
				// 2. If this user exists in our DB. (If not, create)
				user.getRoles()
						.add(new UserRoles(user, studentRole));
				userService.save(user);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failure store CSV data: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save(
			MultipartFile file,
			long courseId
	) {
		Role   studentRole    = roleService.findByName(RoleType.STUDENT.name());
		Course relevantCourse = courseService.findCourseById(courseId);
		try {
			List<User> users = CsvHelper.csvToStudents(file);
			for (User user : users) {
				// we should check to see:
				// 1. if this user exists in Okta. (If not, create)
				// 2. If this user exists in our DB. (If not, create)
				// 3. If this user is already attached to this course. (If not, attach)
				user.getRoles()
						.add(new UserRoles(user, studentRole));
				user = userService.save(user);
				user.getCourses()
						.add(new UserCourses(user, relevantCourse));
				userService.save(user);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failure to store CSV data: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
