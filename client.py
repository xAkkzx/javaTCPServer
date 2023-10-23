import socket
import select

# IP address and port of the server you want to connect to
server_ip = "192.168.1.121"  # Modify with the IP address of your server
server_port = 8000  # Modify with the port of your server

# Create a socket object
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# Set a timeout for the socket
client_socket.settimeout(5)  # 5-second timeout (adjust as needed)

try:
    client_socket.connect((server_ip, server_port))
    print(f"Connected to server {server_ip}:{server_port}")

    # Send data to the server
    latitude1 = input("Enter latitude1: ")
    longitude1 = input("Enter longitude1: ")
    latitude2 = input("Enter latitude2: ")
    longitude2 = input("Enter longitude2: ")
        
    # Create a string with coordinates
    coordinates = f"{latitude1},{longitude1};{latitude2},{longitude2}"
    coordinates = coordinates+"\n"
    print(coordinates)
    client_socket.send(coordinates.encode())

    # Wait for data from the server with a timeout
    ready_to_read, _, _ = select.select([client_socket], [], [], 5)

    if ready_to_read:
        data = client_socket.recv(1024)
        print(f"Received from the server: {data.decode()}")
    else:
        print("Timeout reached while waiting for data from the server.")

except (ConnectionRefusedError, socket.timeout):
    print("Unable to connect to the server or a timeout occurred.")
finally:
    # Close the connection
    client_socket.close()