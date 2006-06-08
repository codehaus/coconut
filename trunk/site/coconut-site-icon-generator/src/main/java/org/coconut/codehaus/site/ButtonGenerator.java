/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.codehaus.site;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ButtonGenerator {
    private String icon = "C:/projects/coconut-site/iconexperience/application_basics/16x16/shadow/";

    private String backgroundPath = "C:/projects/coconut-site/templates/";

    private String outputDirectory = "c:/tmp/";

    public static void main(String[] args) throws IOException {
        new ButtonGenerator().generateTopButton("disk_blue", "download");
        new ButtonGenerator().generateTopButton("about", "about");
        new ButtonGenerator().generateTopButton("documents", "docs");
        new ButtonGenerator().generateTopButton("question_and_answer", "faq");
        new ButtonGenerator().generateTopButton("../../../objects_and_people/16x16/shadow/houses", "projects");
        
    }

    public void generateTopButton(String icon, String text) throws IOException {
        generate0(loadBackgroundIcon("blank_tab.png"), icon, text, text);
        generate0(loadBackgroundIcon("blank_tabov.png"), icon, text, text
                + "ov");
    }

    private void generate0(BufferedImage image, String icon, String text,
            String outputFile) throws IOException {
        Image image2 = loadIcon(icon);
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(image2, 12, 3, null);
        Font font = new Font("Tahoma", Font.BOLD, 12);
        g2.setFont(font);
        g2.setColor(new Color(102, 102, 102));
        g2.drawString(text, 31, 15);
        ImageIO.write(image, "png", new File(outputDirectory + outputFile
                + ".png"));
    }

    private Image loadIcon(String name) throws IOException {
        return ImageIO.read(new File(icon + name + ".png"));
    }

    private BufferedImage loadBackgroundIcon(String name) throws IOException {
        return ImageIO.read(new File(backgroundPath + name));
    }
}
