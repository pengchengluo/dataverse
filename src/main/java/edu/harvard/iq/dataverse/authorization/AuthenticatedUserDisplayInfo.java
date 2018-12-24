package edu.harvard.iq.dataverse.authorization;

import edu.harvard.iq.dataverse.authorization.users.AuthenticatedUser;
import java.util.Objects;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author gdurand
 */
public class AuthenticatedUserDisplayInfo extends RoleAssigneeDisplayInfo {

    @NotBlank(message = "{user.lastName}")
    private String lastName;
    @NotBlank(message = "{user.firstName}")
    private String firstName;
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
    
    private AuthenticatedUser.UserType userType;
    
    /*
     * @todo Shouldn't we persist the displayName too? It still exists on the
     * authenticateduser table.
     */
    public AuthenticatedUserDisplayInfo(String firstName, String lastName, String emailAddress, String affiliation, String position) {
        super(firstName + " " + lastName,emailAddress,affiliation);
        if(cn.edu.pku.lib.dataverse.util.StringUtils.isChinese(lastName)){
            this.setTitle(lastName+firstName);
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;        
        this.userType = AuthenticatedUser.UserType.ORDINARY;
    }

    public AuthenticatedUserDisplayInfo() {
        super("","","");
        firstName="";
        lastName="";
        position="";
        this.userType = AuthenticatedUser.UserType.ORDINARY;
    }

    
    /**
     * Copy constructor (old school!)
     * @param src the display info {@code this} will be a copy of.
     */
    public AuthenticatedUserDisplayInfo( AuthenticatedUserDisplayInfo src ) {
        this( src.getFirstName(), src.getLastName(), src.getEmailAddress(), src.getAffiliation(), src.getPosition());
    }
    
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public AuthenticatedUser.UserType getUserType() {
        return userType;
    }

    public void setUserType(AuthenticatedUser.UserType userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "AuthenticatedUserDisplayInfo{firstName=" + firstName + ", lastName=" + lastName + ", position=" + position + ", email=" + getEmailAddress() + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.firstName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AuthenticatedUserDisplayInfo other = (AuthenticatedUserDisplayInfo) obj;
        if (!Objects.equals(this.lastName, other.lastName)) {
            return false;
        }
        if (!Objects.equals(this.firstName, other.firstName)) {
            return false;
        }
        return Objects.equals(this.position, other.position) && super.equals(obj);
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
    
}

