package cc.mincai.android.desecret;

import org.apache.commons.net.io.Util;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class EmulatorBot {
    public static void main(String[] args) {
        TelnetClient telnet = new TelnetClient();

        try {
            telnet.connect("localhost", 5554);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        feedRemoteInputToLocalOutput(telnet.getInputStream(), System.out);

        double x = 13.24;
        double y = 52.31;

        PrintWriter pw = new PrintWriter(telnet.getOutputStream());

        for(int i = 0; i < 100; i++) {
            String s = "geo fix " + x + " " + y;
            pw.write(s + "\n");
            pw.flush();

            System.out.println("sent: " + s);

            x+= 0.01;
            y += 0.01;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            telnet.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }

    public static void feedRemoteInputToLocalOutput(final InputStream remoteInput,
                                                    final OutputStream localOutput) {
        Thread writer = new Thread() {
            @Override
            public void run() {
                try {
                    Util.copyStream(remoteInput, localOutput);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        };

        writer.setPriority(Thread.currentThread().getPriority() + 1);
        writer.start();
    }
}
