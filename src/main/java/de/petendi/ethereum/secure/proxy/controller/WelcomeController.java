package de.petendi.ethereum.secure.proxy.controller;

/*-
 * #%L
 * Ethereum Secure Proxy
 * %%
 * Copyright (C) 2016 P-ACS UG (haftungsbeschränkt)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import de.petendi.seccoco.Seccoco;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
@RequestMapping("/")
public class WelcomeController {


    private static final String TEMPLATE = "<html><head/><body><div style=\"display: flex;justify-content: center;\">" +
            "<div style=\"width: 270px;\"><img src=\"qr.png\" alt=\"QR Code\" style=\"display: block;margin-left: auto;margin-right: auto;\"/>" +
            "<p align=\"center\"><strong>#FILLME#</strong></p><br/><p align=\"center\">Secured by <a href=\"http://www.seccoco.com\" target=\"_blank\">Seccoco</a></p></div></div></body></html>";


    private Seccoco seccoco;
    private byte[] qrcode;
    private String message;

    @Autowired
    WelcomeController(Seccoco seccoco) {
        this.seccoco = seccoco;
        init();
    }

    private void init() {
        String identity = seccoco.identities().getOwnIdentity().getFingerPrint().replaceAll("..(?!$)", "$0 ").toUpperCase();
        String wrapped = insertLinebreaks(identity,10);
        message = TEMPLATE.replace("#FILLME#", wrapped);
        QRCodeWriter writer = new QRCodeWriter();
        int size = 250;
        BitMatrix matrix = null;
        try {
            matrix = writer.encode(seccoco.identities().getOwnIdentity().getFingerPrint().toUpperCase(), BarcodeFormat.QR_CODE, size, size);
        } catch (WriterException e) {
            throw new IllegalArgumentException(e);
        }
        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", byteArrayOutputStream);
            qrcode = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(byteArrayOutputStream);
        }
    }

    @ResponseBody()
    @RequestMapping(method = RequestMethod.GET, produces = "text/html")
    public String get() {
        return message;
    }

    @ResponseBody
    @RequestMapping(value = "qr.png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getQrCode() throws IOException {
        return qrcode;
    }

    private static String insertLinebreaks(String s, int charsPerLine) {
        char[] chars = s.toCharArray();
        int lastLinebreak = 0;
        boolean wantLinebreak = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (wantLinebreak && chars[i] == ' ') {
                sb.append("<br/><br/>");
                lastLinebreak = i;
                wantLinebreak = false;
            } else {
                sb.append(chars[i]);
            }
            if (i - lastLinebreak + 1 == charsPerLine)
                wantLinebreak = true;
        }
        return sb.toString();
    }

}
