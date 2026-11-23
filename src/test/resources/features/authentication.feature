@ui @authentication
Feature: Shopper authentication
  As a shopper
  I want to log in to my account securely
  So that I can check out and see my order details

  @smoke
  Scenario: Registered shopper logs in and logs out
    Given a registered shopper
    When the shopper logs in with their own credentials
    Then the shopper is logged in under their own name
    When the shopper logs out
    Then the shopper is back on the login page

  Scenario Outline: Login is rejected for <case>
    Given the shopper is on the login page
    When the shopper logs in with email "<email>" and password "<password>"
    Then the login error "Your email or password is incorrect!" is shown

    Examples:
      | case               | email                                    | password         |
      | an unknown email   | not.registered.qa.framework@example.com  | Password@123     |
      | a wrong password   | qa.framework.wrong.password@example.com  | definitely-wrong |

  Scenario: Malformed email is blocked by the browser before submitting
    Given the shopper is on the login page
    When the shopper logs in with email "not-an-email" and password "Password@123"
    Then the browser keeps the shopper on the login form

  Scenario: Signing up with an email that is already registered is rejected
    Given a registered shopper
    When someone tries to sign up as "Someone Else" with the shopper's email
    Then the signup error "Email Address already exist!" is shown
