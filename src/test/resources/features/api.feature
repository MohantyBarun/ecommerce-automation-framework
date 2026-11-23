@api
Feature: Public shop API
  The automationexercise.com API always answers HTTP 200 and reports
  the real outcome in the "responseCode" and "message" fields.

  @smoke
  Scenario: Account lifecycle through the API
    Given a new shopper who is not registered yet
    When the shopper's account is created through the API
    Then the API responds with code 201 and message "User created!"
    When the shopper's login is verified through the API
    Then the API responds with code 200 and message "User exists!"
    When the shopper's account is deleted through the API
    Then the API responds with code 200 and message "Account deleted!"
    When the shopper's login is verified through the API
    Then the API responds with code 404 and message "User not found!"

  Scenario: An email can only be registered once
    Given a registered shopper
    When the shopper's account is created through the API
    Then the API responds with code 400 and message "Email already exists!"

  Scenario: Login verification fails with a wrong password
    Given a registered shopper
    When the shopper's login is verified through the API with a wrong password
    Then the API responds with code 404 and message "User not found!"

  @smoke
  Scenario: The product list is complete and well formed
    When the full product list is requested
    Then the API responds with code 200
    And every product has an id, a name and a price in rupees

  Scenario Outline: Invalid requests are rejected - <case>
    When <request>
    Then the API responds with code <code> and message "<message>"

    Examples:
      | case               | request                                    | code | message                                                             |
      | wrong HTTP method  | the product list endpoint is called with POST | 405  | This request method is not supported.                               |
      | missing parameter  | products are searched without a search term   | 400  | Bad request, search_product parameter is missing in POST request.  |
