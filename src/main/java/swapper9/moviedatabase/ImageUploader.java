package swapper9.moviedatabase;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class ImageUploader {

  public byte[] upload(String url) {
    File file = new File(url);
    byte[] bFile = new byte[(int) file.length()];
    try {
      FileInputStream fileInputStream = new FileInputStream(file);
      fileInputStream.read(bFile);
      fileInputStream.close(); }
    catch (Exception e) {
      e.printStackTrace(); }
    return bFile;
  }

  public void load() throws IOException {
    URL url = new URL("http://www.mkyong.com/image/mypic.jpg");
    Image image = ImageIO.read(url);
  }

}

