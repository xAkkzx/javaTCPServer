import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        int port = 8000;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread clientThread = new Thread(() -> handleClient(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void handleClient(Socket clientSocket) {
        try {
            // Get the client's address and port
            String clientAddress = clientSocket.getInetAddress().getHostAddress();
            int clientPort = clientSocket.getPort();
            System.out.println("Client connected from " + clientAddress + ":" + clientPort);

            // Receive the latitude and longitude coordinates from the client
            InputStream inputStream = clientSocket.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            String receivedData = scanner.nextLine();
            String[] coordinates = receivedData.split(";");
            System.out.println("Received coordinates: " + receivedData);
            
            if (coordinates.length != 2) {
                System.out.println("Invalid packet: " + receivedData);
            } else {
                String[] coordinates1 = coordinates[0].split(",");
                double lat1 = Double.parseDouble(coordinates1[0]);
                double lon1 = Double.parseDouble(coordinates1[1]);
                
                String[] coordinates2 = coordinates[1].split(",");
                double lat2 = Double.parseDouble(coordinates2[0]);
                double lon2 = Double.parseDouble(coordinates2[1]);
                
                double distance = distanceCalc(lat1, lon1, lat2, lon2);
                OutputStream outputStream = clientSocket.getOutputStream();
                String response = String.valueOf(distance);
                outputStream.write(response.getBytes());
            }
        } catch (IOException e) {
            System.err.println("Error while handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static double distanceCalc(double lat1, double lon1, double lat2, double lon2) {
        // Calculate the distance between the two coordinates using the Haversine formula
        double R = 6371; // Radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
