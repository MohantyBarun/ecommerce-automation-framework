@ui @cart
Feature: Shopping cart
  As a shopper
  I want the cart to reflect exactly what I chose
  So that I am charged the right amount

  @smoke
  Scenario Outline: Product added from the catalogue appears in the cart - <name>
    Given the shopper is browsing the product catalogue
    When the shopper adds product <id> to the cart and opens the cart
    Then the cart contains only "<name>" priced "<price>" with quantity 1

    Examples:
      | id | name             | price    |
      | 1  | Blue Top         | Rs. 500  |
      | 2  | Men Tshirt       | Rs. 400  |
      | 3  | Sleeveless Dress | Rs. 1000 |

  Scenario: Quantity chosen on the product page is applied to the cart total
    Given the shopper is viewing product 1
    When the shopper adds 4 of it to the cart
    Then the cart shows quantity 4 and a total of 4 times the unit price

  Scenario: Removing every product empties the cart
    Given the shopper has these products in the cart:
      | 1 |
      | 2 |
    When the shopper removes every product from the cart
    Then the cart is empty
