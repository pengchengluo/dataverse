package edu.harvard.iq.dataverse.authorization.providers.builtin;

import edu.harvard.iq.dataverse.ValidateEmail;
import edu.harvard.iq.dataverse.ValidateUserName;
import edu.harvard.iq.dataverse.authorization.AuthenticatedUserDisplayInfo;
import edu.harvard.iq.dataverse.authorization.users.AuthenticatedUser.UserType;
import static edu.harvard.iq.dataverse.util.StringUtil.nonEmpty;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author xyang
 * @author mbarsinai
 */
@NamedQueries({
		@NamedQuery( name="BuiltinUser.findAll",
				query = "SELECT u FROM BuiltinUser u ORDER BY u.lastName"),
		@NamedQuery( name="BuiltinUser.findByUserName",
				query = "SELECT u FROM BuiltinUser u WHERE u.userName=:userName"),
		@NamedQuery( name="BuiltinUser.findByEmail",
				query = "SELECT o FROM BuiltinUser o WHERE LOWER(o.email) = LOWER(:email)"),
		@NamedQuery( name="BuiltinUser.listByUserNameLike",
				query = "SELECT u FROM BuiltinUser u WHERE u.userName LIKE :userNameLike")
})
@Entity
@Table(indexes = {@Index(columnList="lastName")})  // for sorting the NamedQuery BuiltinUser.findAll
public class BuiltinUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Please enter a username.")
    @Size(min=2, max=60, message ="Username must be between 2 and 60 characters.")
    @ValidateUserName(message = "Found an illegal character(s). Valid characters are a-Z, 0-9, '_', '-', and '.'.")
    @Column(nullable = false, unique=true)  
    private String userName;

    @NotBlank(message = "Please enter a valid email address.")
    @ValidateEmail(message = "Please enter a valid email address.")
    @Column(nullable = false, unique=true)    
    private String email;

    @NotBlank(message = "Please enter your first name.")
    private String firstName;

    @NotBlank(message = "Please enter your last name.")
    private String lastName;
    
    private int passwordEncryptionVersion; 
    private String encryptedPassword;

    private String affiliation;
    private String position;
    
    private String department;
    private String speciality;
    private String researchInterest;
    
    private String gender;
    private String education;
    private String professionalTitle;
    private String supervisor;
    private String certificateType;
    private String certificateNumber;
    private String officePhone;
    private String cellphone;
    private String otherEmail;
    private String country;
    private String province;
    private String city;
    private String address;
    private String zipCode;
    private UserType userType;
    
    public void updateEncryptedPassword( String encryptedPassword, int algorithmVersion ) {
        setEncryptedPassword(encryptedPassword);
        setPasswordEncryptionVersion(algorithmVersion);
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEncryptedPassword() {
        return encryptedPassword;
    }
    
    /**
     * JPA-use only. Humans should call {@link #updateEncryptedPassword(java.lang.String, int)}
     * and update the password and the algorithm at the same time.
     * 
     * @param encryptedPassword
     * @deprecated
     */
    @Deprecated()
    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
    
    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    
    public String getDisplayName(){
        return this.getFirstName() + " " + this.getLastName(); 
    }
    
    public void applyDisplayInfo( AuthenticatedUserDisplayInfo inf ) {
        setFirstName(inf.getFirstName());
        setLastName(inf.getLastName());
        if ( nonEmpty(inf.getEmailAddress()) ) {
            setEmail(inf.getEmailAddress());
        }
        if ( nonEmpty(inf.getAffiliation()) ) {
            setAffiliation( inf.getAffiliation() );
        }
        if ( nonEmpty(inf.getPosition()) ) {
            setPosition( inf.getPosition());
        }
        if (nonEmpty(inf.getDepartment())){
            this.setDepartment(inf.getDepartment());
        }
        if (nonEmpty(inf.getSpeciality())){
            this.setSpeciality(inf.getSpeciality());
        }
        if (nonEmpty(inf.getResearchInterest())){
            this.setResearchInterest(inf.getResearchInterest());
        }
        if (nonEmpty(inf.getGender())){
            this.setGender(inf.getGender());
        }
        if (nonEmpty(inf.getEducation())){
            this.setEducation(inf.getEducation());
        }
        if (nonEmpty(inf.getProfessionalTitle())){
            this.setProfessionalTitle(inf.getProfessionalTitle());
        }
        if (nonEmpty(inf.getSupervisor())){
            this.setSupervisor(inf.getSupervisor());
        }
        if (nonEmpty(inf.getCertificateType())){
            this.setCertificateType(inf.getCertificateType());
        }
        if (nonEmpty(inf.getCertificateNumber())){
            this.setCertificateNumber(inf.getCertificateNumber());
        }
        if (nonEmpty(inf.getOfficePhone())){
            this.setOfficePhone(inf.getOfficePhone());
        }
        if (nonEmpty(inf.getCellphone())){
            this.setCellphone(inf.getCellphone());
        }
        if (nonEmpty(inf.getOtherEmail())){
            this.setOtherEmail(inf.getOtherEmail());
        }
        if (nonEmpty(inf.getCountry())){
            this.setCountry(inf.getCountry());
        }
        if (nonEmpty(inf.getProvince())){
            this.setProvince(inf.getProvince());
        }
        if (nonEmpty(inf.getCity())){
            this.setCity(inf.getCity());
        }
        if (nonEmpty(inf.getAddress())){
            this.setAddress(inf.getAddress());
        }
        if (nonEmpty(inf.getZipCode())){
            this.setZipCode(inf.getZipCode());
        }
        setUserType(inf.getUserType());
    }
    
    public AuthenticatedUserDisplayInfo getDisplayInfo() {
        AuthenticatedUserDisplayInfo audi = new AuthenticatedUserDisplayInfo(getFirstName(), getLastName(), getEmail(), getAffiliation(), getPosition() );
        audi.setDepartment(department);
        audi.setSpeciality(speciality);
        audi.setResearchInterest(researchInterest);
        audi.setGender(gender);
        audi.setEducation(education);
        audi.setProfessionalTitle(professionalTitle);
        audi.setSupervisor(supervisor);
        audi.setCertificateType(certificateType);
        audi.setCertificateNumber(certificateNumber);
        audi.setOfficePhone(officePhone);
        audi.setCellphone(cellphone);
        audi.setOtherEmail(otherEmail);
        audi.setCountry(country);
        audi.setProvince(province);
        audi.setCity(city);
        audi.setAddress(address);
        audi.setZipCode(zipCode);
        audi.setUserType(userType);
        return audi;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BuiltinUser)) {
            return false;
        }
        BuiltinUser other = (BuiltinUser) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

	@Override
	public String toString() {
		return "BuiltinUser{" + "id=" + id + ", userName=" + userName + ", email=" + email + '}';
	}

    public int getPasswordEncryptionVersion() {
        return passwordEncryptionVersion;
    }

    public void setPasswordEncryptionVersion(int passwordEncryptionVersion) {
        this.passwordEncryptionVersion = passwordEncryptionVersion;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getResearchInterest() {
        return researchInterest;
    }

    public void setResearchInterest(String researchInterest) {
        this.researchInterest = researchInterest;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getProfessionalTitle() {
        return professionalTitle;
    }

    public void setProfessionalTitle(String professionalTitle) {
        this.professionalTitle = professionalTitle;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getOtherEmail() {
        return otherEmail;
    }

    public void setOtherEmail(String otherEmail) {
        this.otherEmail = otherEmail;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
