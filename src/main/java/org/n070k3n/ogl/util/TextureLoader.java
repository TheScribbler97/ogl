package org.n070k3n.ogl.util;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.nio.ByteBuffer;

public class TextureLoader
{
    public static Image load(String name) throws IOException
    {
        //
        BufferedImage tmp = ImageIO.read(TextureLoader.class.getClassLoader().getResourceAsStream(name));
        BufferedImage img = new BufferedImage(tmp.getWidth(),tmp.getHeight(), BufferedImage.TYPE_INT_ARGB);
        img.getGraphics().drawImage(tmp,0,0,null);
        
        byte[] data = new byte[img.getWidth()*img.getHeight()*4];
        int[] vals = new int[4];
        Raster raster = img.getRaster();
        for(int y = 0; y < img.getHeight(); y++)
        {
            for(int x = 0; x < img.getWidth(); x++)
            {
                raster.getPixel(x, (img.getHeight()-1)-y, vals);
                data[y*img.getWidth()*4+x*4+0] = (byte)vals[0];
                data[y*img.getWidth()*4+x*4+1] = (byte)vals[1];
                data[y*img.getWidth()*4+x*4+2] = (byte)vals[2];
                data[y*img.getWidth()*4+x*4+3] = (byte)vals[3];
            }
        }
        
        return new Image(img.getWidth(), img.getHeight(), data);
    }
    
    public static class Image
    {
        public final int width;
        public final int height;
        public final ByteBuffer data;
        
        private Image(int width, int height, byte[] data)
        {
            this.width = width;
            this.height = height;
            this.data = BufferUtils.createByteBuffer(data.length);
            this.data.put(data);
            this.data.flip();
        }
    }
}