package com.lambdaschool.oktafoundation.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * The entity allowing interaction with the users table
 */
@Entity
@Table(name = "users")
@JsonIgnoreProperties(value = {"programs", "courses", "roles"}, allowSetters = true)
public class User
		extends Auditable {

	/**
	 * The primary key (long) of the users table.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long             userId;
	/**
	 * The username (String). Cannot be null and must be unique
	 */
	@NotNull
	@Column(unique = true)
	private String           username;
	//
	@NotNull
	@Column(unique = true)
	private String           email;
	//
	private String           firstName;
	//
	private String           lastName;
	//
	private String           phoneNumber;
	//
	private RoleType         roleType;
	//
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnoreProperties(value = "user", allowSetters = true)
	private Set<Program>     programs = new HashSet<>();
	//
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnoreProperties(value = "user", allowSetters = true)
	private Set<UserCourses> courses  = new HashSet<>();
	/**
	 * Part of the join relationship between user and role
	 * connects users to the user role combination
	 */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value = "user", allowSetters = true)
	private Set<UserRoles>   roles    = new HashSet<>();

	/**
	 * Default constructor used primarily by the JPA.
	 */
	public User() {
	}

	public User(
			@NotNull String username,
			@NotNull String email,
			String firstName,
			String lastName,
			String phoneNumber
	) {
		this.username    = username;
		this.firstName   = firstName;
		this.email       = email;
		this.lastName    = lastName;
		this.phoneNumber = phoneNumber;
	}


	/**
	 * Given the params, create a new user object
	 * <p>
	 * userid is autogenerated
	 *
	 * @param username The username (String) of the user
	 */
	public User(String username) {
		setUsername(username);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstname) {
		this.firstName = firstname;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastname) {
		this.lastName = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phonenumber) {
		this.phoneNumber = phonenumber;
	}

	public Set<Program> getPrograms() {
		return programs;
	}

	public void setPrograms(Set<Program> programs) {
		this.programs = programs;
	}

	/**
	 * Getter for userid
	 *
	 * @return the userid (long) of the user
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * Setter for userid. Used primary for seeding data
	 *
	 * @param userid the new userid (long) of the user
	 */
	public void setUserId(long userid) {
		this.userId = userid;
	}

	/**
	 * Getter for username
	 *
	 * @return the username (String) lowercase
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * setter for username
	 *
	 * @param username the new username (String) converted to lowercase
	 */
	public void setUsername(String username) {
		this.username = username.toLowerCase();
	}


	/**
	 * Getter for user role combinations
	 *
	 * @return A list of user role combinations associated with this user
	 */
	public Set<UserRoles> getRoles() {
		return roles;
	}

	/**
	 * Setter for user role combinations
	 *
	 * @param roles Change the list of user role combinations associated with this user to this one
	 */
	public void setRoles(Set<UserRoles> roles) {
		this.roles = roles;
	}

	public Set<UserCourses> getCourses() {
		return courses;
	}

	public void setCourses(Set<UserCourses> courses) {
		this.courses = courses;
	}

	@JsonIgnore
	public void setName(String name) {
		String[] splitName = name.split(" ");
		this.firstName = splitName[0];
		if (splitName.length > 1) {
			this.lastName = splitName[1];
		}
	}

	/**
	 * Internally, user security requires a list of authorities, roles, that the user has. This method is a simple way to provide those.
	 * Note that SimpleGrantedAuthority requests the format ROLE_role name all in capital letters!
	 *
	 * @return The list of authorities, roles, this user object has
	 */
	@JsonIgnore
	public List<SimpleGrantedAuthority> getAuthority() {
		List<SimpleGrantedAuthority> rtnList = new ArrayList<>();

		for (UserRoles r : this.roles) {
			String myRole = "ROLE_" + r.getRole()
					.getName()
					.toUpperCase();
			rtnList.add(new SimpleGrantedAuthority(myRole));
		}

		return rtnList;
	}

	@JsonIgnore
	public SimpleGrantedAuthority getPriorityAuthority() {
		boolean isAdmin   = false;
		boolean isTeacher = false;
		boolean isStudent = false;

		for (UserRoles r : this.roles) {
			RoleType currentRoleType = r.getRole()
					.getRoleType();
			if (currentRoleType != null) {
				if (currentRoleType == RoleType.ADMIN) {
					isAdmin = true;
					break;
				} else if (currentRoleType == RoleType.TEACHER) {
					isTeacher = true;
				} else if (currentRoleType == RoleType.STUDENT) {
					isStudent = true;
				}
			}
		}

		if (isAdmin) {
			this.roleType = RoleType.ADMIN;
			return new SimpleGrantedAuthority(RoleType.ADMIN.name());
		}
		if (isTeacher) {
			this.roleType = RoleType.TEACHER;
			return new SimpleGrantedAuthority(RoleType.TEACHER.name());
		}
		if (isStudent) {
			this.roleType = RoleType.STUDENT;
			return new SimpleGrantedAuthority(RoleType.STUDENT.name());
		}

		this.roleType = null;
		return null;

	}

	@JsonIgnore
	public RoleType getRoleType() {
		return this.roleType;
	}

	public RoleType getRole() {
		boolean isAdmin   = false;
		boolean isTeacher = false;
		boolean isStudent = false;
		for (UserRoles r : this.roles) {
			RoleType currentRoleType = r.getRole()
					.getRoleType();
			if (currentRoleType != null) {
				if (currentRoleType == RoleType.ADMIN) {
					isAdmin = true;
				} else if (currentRoleType == RoleType.TEACHER) {
					isTeacher = true;
				} else if (currentRoleType == RoleType.STUDENT) {
					isStudent = true;
				}
			}
		}
		if (isAdmin) {
			this.roleType = RoleType.ADMIN;
			return RoleType.ADMIN;
		}
		if (isTeacher) {
			this.roleType = RoleType.TEACHER;
			return RoleType.TEACHER;
		}
		if (isStudent) {
			this.roleType = RoleType.STUDENT;
			return RoleType.STUDENT;
		}

		return null;

	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User) o;
		return getUserId() == user.getUserId() && getUsername().equals(user.getUsername()) &&
		       getEmail().equals(user.getEmail());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getUserId(), getUsername(), getEmail());
	}

	@Override
	public String toString() {
		return "User{" + "userid=" + userId + ", username='" + username + '\'' + ", email='" + email + '\'' +
		       ", firstname='" + firstName + '\'' + ", lastname='" + lastName + '\'' + ", phonenumber='" + phoneNumber +
		       '\'' + ", roleType=" + roleType + " courses=" + courses + '}';
	}

}
