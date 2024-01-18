# Members
| First Name | Last Name  | 
|------------|------------|
| Ansen      | Weng       |
| Thijs      | Houben     | 
| Hidde      | van Luenen |
| Vlad       | Popescu    |
| Petra      | Gulyás     |
| Ioana      | Forfota    |

# YumYumNow

YumYumNow is a food delivery service that consists of a variety of microservices to handle the back-end. These microservices are all developed separately in different teams, with minimal communication between them. Communication between the microservices is handled through endpoints, which are clarified in the respective YAML documents. This project lasted from November 14 2023, to January 18 2024.

# YumYumNow Orders

Within the YumYumNow project, our group (11a) worked on the Orders microservice. Hence this repository is the codebase for this microservice. It consists of production code and tests, and is made to work together with the Users microservice and the Delivery microservice. All tests can be run by building  


## Running the microservices

You can run the two microservices individually by starting the Spring applications. Then, you can use *Postman* to perform the different requests:

Register:
![image](instructions/register.png)

Authenticate:
![image](instructions/authenticate.png)

Hello:
![image](instructions/hello.png)