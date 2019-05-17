package de.coop.tgvertretung.utils;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectSerializer {

    public static String serialize(Object object) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(outputStream);
        stream.writeObject(object);
        stream.flush();
        stream.close();
        byte[] data = outputStream.toByteArray();
        outputStream.close();
        return Base64.encodeToString(data, 1);
    }

    public static Object deserialize(String data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.decode(data, 1));
        ObjectInputStream stream = new ObjectInputStream(inputStream);
        Object obj = stream.readObject();
        stream.close();
        inputStream.close();
        return obj;
    }

}
