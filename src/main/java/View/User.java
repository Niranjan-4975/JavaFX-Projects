package View;

public class User {
    private final int accNo;
    private final int regNo;
    private final String name;
    private final byte[] userImage;

    public User(int accountNo, int registrationNo, String name, byte[] userImage){
        this.accNo = accountNo;
        this.regNo = registrationNo;
        this.name = name;
        this.userImage = userImage;
    }

    public int getAccNo() {
        return accNo;
    }

    public int getRegNo() {
        return regNo;
    }

    public String getName() {
        return name;
    }

    public byte[] getUserImage() {
        return userImage;
    }
}
