Feature: Application need to be logged

	Different services has different types of actions. So we need a centralized logging module to log all the back-end service actions.

	Scenario: Logging service is up and running
		Given logging service is running
		When I put a log request to "log" path
		Then I should see http code 200 returned