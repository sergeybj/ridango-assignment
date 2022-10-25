## Comments from Sergey, 25.10.2022:

Postman can be used for testing implemented API.
Swagger2 is also enabled for testing API:
![image](https://user-images.githubusercontent.com/11816438/197779321-a5b7db49-4aeb-44a6-be7e-acb8e45d7ee9.png)

Payment can be done via POST, sending JSON to "localhost:8080/api/payment/process":

Request body
        ```
{
    "senderAccountId": "100221",
    "receiverAccountId": "100222",
    "amount": "100.00"
}
        ```

All accounts data is available via:
GET: localhost:8080/api/account/searchAll

All processed payments data is available via:
GET: localhost:8080/api/payment/searchAllProcessedPayments

If result is OK, then success PaymentDTO is retrieved:
        ```
{
    "senderAccountId": "100221",
    "receiverAccountId": "100222",
    "amount": "1.00",
    "timestamp": "2022-10-25T16:21:47.959926800"
}
        ```

If one of 9 possible exceptions is thrown, these are propagated in HTTP response with status 400:

![image](https://user-images.githubusercontent.com/11816438/197784898-ca9aaa2f-7931-4a6e-9993-9a2a3068caae.png)

Possible exceptions (business errors):
    SENDER_EMPTY_OR_INVALID_FORMAT("SE-001", "sender account is not found in request message or has invalid format"),
    SENDER_NOT_FOUND("SE-002", "sender account does not exist"),
    RECEIVER_EMPTY_OR_INVALID_FORMAT("RE-001", "receiver account is not found in request message or has invalid format"),
    RECEIVER_NOT_FOUND("RE-002", "receiver account does not exist"),
    AMOUNT_EMPTY_OR_INVALID_FORMAT("AM-001", "amount for transaction is not found in request message or has invalid format"),
    AMOUNT_ZERO("AM-002", "amount for transaction equals to zero"),
    AMOUNT_INSUFFICIENT("AM-003", "amount for transaction exceeds current sender's balance"),
    COLLISION_OF_SENDER_AND_RECEIVER("CO-001", "sender and receiver are the same"),
    COLLISION_OF_TRANSACTIONS("CO-002", "another transaction is in progress");


## To build a project
* Java 1.8 or newer
* Gradle 6.0 or newer

## About default setup
* `gradle assemble` to initialize a project
* You can start Spring application via your favourite ide or using `gradle bootjar` and then executing previously created jar
* It uses h2 for db so no need to setup any external database. To access running application db http://localhost:8080/h2-console/login.jsp (login parameters are in application.properties file)
* schema.sql will generate sql tables when Spring application starts
* If you have any problems to run this pre-setup Spring application then feel free to create your own Spring project

## Assignment
* Write endpoint POST `/payment` with json body 
    * Request json example:
        ```
        {
            "senderAccountId": "1",
            "receiverAccountId: "2",
            "amount": "100.00"
        }
      ```
* Requirements
    * Amount must be a number > 0 and can have two decimal places
	* senderAccountId, receiverAccountId are account table id-s 
    * Account balance cannot go negative.
	* Money is withdrawn from sender account and deposited into receiver account 
    * And everything else what you think that is important
* Example
	* Before payment
		* Sender account balance 100.00
		* Receiver account balance 100.00
	* Do payment
		*  POST `/payment`
			```
			{
				"senderAccountId": "1",
				"receiverAccountId: "2",
				"amount": "100.00"
			}
			```
	* After payment
		* Sender account balance 0.00
		* Receiver account balance 200.00
    
