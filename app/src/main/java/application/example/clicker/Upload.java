package application.example.clicker;

public class Upload {
    private String mImageUri;

    public Upload(){

    }

    public Upload(String imageUri){
        mImageUri = imageUri;
    }

    public String getmImageUri(){
        return mImageUri;
    }

    public void setmImageUri(String imageUri){
        mImageUri = imageUri;
    }


}
