Feature: Questionnaire

Scenario: As a citizen I am able to complete the C-reaktivt Protein questionnaire
	Given I am logged in as Linda with password abcd1234
	And I start the "C-reaktivt Protein" questionnaire
	When I answer "10"
	And I go to the next page
	Then I can send the results

Scenario: As a citizen I am able to complete the Hæmoglobin indhold i blod questionnaire
	Given I am logged in as Linda with password abcd1234
	And I start the "Hæmoglobin indhold i blod" questionnaire
	When I answer "1"
	And I choose "OK"
	Then I can send the results

Scenario: Rejse-sætte-sig-test questionnaire
	Given I am logged in as Linda with password abcd1234
	And I start the "Rejse-sætte-sig" questionnaire
	And I read the text and proceed
	And I press "Ja"
	And I read the text and proceed
	And I wait for 45 seconds
	And I wait for 32 seconds
	When I answer "5"
	And I go to the next page
	And I select "Hænderne på stolens armlæn"
	And I go to the next page
	Then I can send the results

Scenario: Blodsukker (manuel)
	Given I am logged in as Linda with password abcd1234
	And I start the "Blodsukker" questionnaire
	And I read the text and proceed
	When I answer "5.3"
	And I select "Før måltid"
	And I go to the next page
	Then I can send the results