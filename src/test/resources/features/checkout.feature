@ui @checkout @e2e
Feature: Checkout
  As a logged-in shopper
  I want to pay for the products in my cart
  So that my order is placed and delivered to my address

  @smoke
  Scenario: Logged-in shopper buys two products end to end
    Given a registered shopper is logged in
    And the shopper has these products in the cart:
      | 1 |
      | 2 |
    Then the cart lists the products:
      | Blue Top   |
      | Men Tshirt |
    When the shopper proceeds to checkout
    Then the delivery address matches the shopper's account
    When the shopper places the order with the comment "Automated order, please ignore" and pays by card
    Then the order confirmation "ORDER PLACED!" is shown
