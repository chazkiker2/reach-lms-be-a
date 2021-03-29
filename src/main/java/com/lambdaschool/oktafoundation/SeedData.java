package com.lambdaschool.oktafoundation;


import com.lambdaschool.oktafoundation.models.Module;
import com.lambdaschool.oktafoundation.models.*;
import com.lambdaschool.oktafoundation.repository.TagRepository;
import com.lambdaschool.oktafoundation.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;


/**
 * SeedData puts both known and random data into the database. It implements CommandLineRunner.
 * <p>
 * CoomandLineRunner: Spring Boot automatically runs the run method once and only once
 * after the application context has been loaded.
 */
@Transactional
@ConditionalOnProperty(prefix = "command.line.runner", value = "enabled", havingValue = "true", matchIfMissing = true)
@Component
public class SeedData
		implements CommandLineRunner {

	/**
	 * Connects the Role Service to this process
	 */
	@Autowired
	RoleService roleService;

	/**
	 * Connects the user service to this process
	 */
	@Autowired
	UserService userService;

	@Autowired
	StudentTeacherService studentTeacherService;

	@Autowired
	ProgramService programService;

	@Autowired
	CourseService courseService;

	@Autowired
	ModuleService moduleService;

	@Autowired
	TagRepository tagRepository;

	/**
	 * Generates test, seed data for our application
	 * First a set of known data is seeded into our database.
	 * Second a random set of data using Java Faker is seeded into our database.
	 * Note this process does not remove data from the database. So if data exists in the database
	 * prior to running this process, that data remains in the database.
	 *
	 * @param args The parameter is required by the parent interface but is not used in this process.
	 */
	@Transactional
	@Override
	public void run(String[] args)
	throws Exception {
		roleService.deleteAll();
		Role adminRole   = new Role(RoleType.ADMIN.name(), RoleType.ADMIN);
		Role teacherRole = new Role(RoleType.TEACHER.name(), RoleType.TEACHER);
		Role studentRole = new Role(RoleType.STUDENT.name(), RoleType.STUDENT);

		adminRole   = roleService.save(adminRole);
		teacherRole = roleService.save(teacherRole);
		studentRole = roleService.save(studentRole);

		// PROGRAMS
		Program program1 = new Program("Program1", "12th grade", "This is program 1");
		Program program2 = new Program("Program2", "12th grade", "This is program 2");

		// USER llama001@maildrop.cc
		User llama001 = new User("llama001@maildrop.cc", "llama001@email.com", "llama1", "LLAMA_001", "9876543210");
		llama001.getRoles()
				.add(new UserRoles(llama001, adminRole));
		llama001 = userService.save(llama001);

		// TEACHER USER reach.lms.test+Teach001@gmail.com
		User teacher001 = new User("reach.lms.test+Teach001@gmail.com",
				"reach.lms.test+Teach001@gmail.com",
				"Teacher001",
				"TEACHER_001",
				null
		);

		teacher001.getRoles()
				.add(new UserRoles(teacher001, teacherRole));

		// STUDENT USER reach.lms.test+Student001@gmail.com
		User student001 = new User("reach.lms.test+Student001@gmail.com",
				"reach.lms.test+Student001@gmail.com",
				"Student001",
				"STUDENT_001",
				null
		);

		student001.getRoles()
				.add(new UserRoles(student001, studentRole));

		teacher001 = userService.save(teacher001);
		student001 = userService.save(student001);

		// USER reach.lms.test@gmail.com
		User reachRoot = new User("reach.lms.test@gmail.com",
				"reach.lms.test@gmail.com",
				"reach_root",
				"reach_root",
				"1234567890"
		);
		reachRoot.getRoles()
				.add(new UserRoles(reachRoot, adminRole));
		reachRoot.getPrograms()
				.add(program1);
		reachRoot.getPrograms()
				.add(program2);

		reachRoot = userService.save(reachRoot);

		Tag tag1 = new Tag("Computer Science", "#000000");
		Tag tag2 = new Tag("Higher Education");
		program1.addTag(tag1);
		program1.addTag(tag2);

		program1 = programService.save(reachRoot.getUserid(), program1);
		program2 = programService.save(reachRoot.getUserid(), program2);

		Course course1 = new Course("Course1", "COURSE_1", "This course1", program1);
		Course course2 = new Course("Course2", "COURSE_2", "This is course #2", program1);
		Course course3 = new Course("Course3", "COURSE_3", "This is course #3", program2);

		course1.getUsers()
				.add(new UserCourses(teacher001, course1));
		course2.getUsers()
				.add(new UserCourses(teacher001, course2));
		course1.getUsers()
				.add(new UserCourses(student001, course1));
		course2.getUsers()
				.add(new UserCourses(student001, course2));

		course2.setTag(tag2);


		course1 = courseService.save(course1.getProgram()
				.getProgramid(), course1);
		course2 = courseService.save(course2.getProgram()
				.getProgramid(), course2);
		course3 = courseService.save(course3.getProgram()
				.getProgramid(), course3);

		Module module1 = new Module("Module1", "This is module #1", "Content for module #1", course1);
		Module module2 = new Module("Module2", "This is module #2", "Content for module #2", course1);
		Module module3 = new Module("Module3", "This is module #3", "Content for module #3", course1);
		Module module4 = new Module("Module4", "This is module #4", "Content for module #4", course2);

		module1 = moduleService.save(module1.getCourse()
				.getCourseid(), module1);
		module2 = moduleService.save(module2.getCourse()
				.getCourseid(), module2);
		module3 = moduleService.save(module3.getCourse()
				.getCourseid(), module3);
		module4 = moduleService.save(module4.getCourse()
				.getCourseid(), module4);
	}

}