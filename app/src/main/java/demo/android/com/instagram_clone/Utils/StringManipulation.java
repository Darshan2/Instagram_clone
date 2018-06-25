package demo.android.com.instagram_clone.Utils;

/**
 * Created by Admin on 30-05-2018.
 */

public class StringManipulation {

    public static String expandUserName(String userName) {
        return userName.replace(".", " ");
    }

    public static String condenseUserName(String userName) {
        return userName.replace(" ", ".");
    }

    /**
     * Input -> some description #tag1 anything #tag2 #tag3
     * Output -> #tag1,#tag2,#tag3
     * @param caption
     * @return
     */
    public static String getTags(String caption) {
        String s;
        if(caption.indexOf("#") > 0) {
            StringBuilder sb = new StringBuilder();
            char[] charArray = caption.toCharArray();
            boolean charExist = false;

            for (char ch: charArray) {
                if(ch == '#') {
                    charExist = true;
                    sb.append(ch);
                } else {
                    if(charExist) {
                        sb.append(ch);
                    }
                } if (ch == ' ') {
                    charExist = false;
                }
            }
            s = sb.toString().replace(" ", "")
                    .replace("#", ",#").replace("\n", "");
            return s.substring(1, s.length());
        }
        return caption;
    }


    public static String getStorageCompatibleFilepath(String imageLoaderFilepath) {
        if(imageLoaderFilepath != null && (imageLoaderFilepath.length() > 3)) {
            int index = imageLoaderFilepath.indexOf("///");
            return imageLoaderFilepath.substring(index + 2);
        } else {
            return "";
        }
    }
}
