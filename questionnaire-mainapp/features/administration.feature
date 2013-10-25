Feature: Administration

Scenario: As a client I should not see the administration options
    When I am logged in as Linda with password abcd1234
   	Then I should not see the administration options

Scenario: As a administrator I should see the administration options
    When I am logged in as admin with password admin
   	Then I should see the administration options
