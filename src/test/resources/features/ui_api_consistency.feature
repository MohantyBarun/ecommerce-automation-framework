@hybrid
Feature: UI and API stay consistent
  The web shop and its public API are two views of the same data.
  These scenarios act through one and verify through the other.

  @smoke
  Scenario: An account created through the API can log in through the UI
    Given a registered shopper
    When the shopper logs in with their own credentials
    Then the shopper is logged in under their own name

  Scenario: An account registered through the UI is stored correctly
    Given a new shopper who is not registered yet
    When the shopper registers through the signup form
    Then the confirmation "ACCOUNT CREATED!" is shown and the shopper is logged in
    And the account details returned by the API match what the shopper entered

  Scenario Outline: UI search results for "<term>" match the search API
    Given the shopper is browsing the product catalogue
    When the shopper searches the catalogue for "<term>"
    Then the results section is titled "SEARCHED PRODUCTS"
    And the products shown match the search API results for "<term>"

    Examples:
      | term   |
      | top    |
      | tshirt |
      | dress  |
      | jeans  |
