#encoding:utf-8
When /^I am logged in as (.*) with password (.*)$/ do |user_name, password|
  sleep(2)
  wait_for_element_does_not_exist("android.widget.ProgressBar")
  login user_name, password
end

Then /^I should not see the administration options$/ do
  assert_text('Vis Upload-debug-node?', false)
end

Then /^I should see the administration options$/ do
	assert_text 'Vis Upload-debug-node?'
end

Then /^I start the "(.*?)" questionnaire$/ do |questionnaire_name|
	click_on_text 'Gennemfør måling'
	click_on_text questionnaire_name
	sleep 0.1
end

Then /^I answer "(.*)"$/ do |answer|
  enter_text_into_numbered_field answer, 1
end

Then /^I go to the next page$/ do
  press_button_with_text 'Næste'
end

Then /^I choose "(.*?)"$/ do |button_text|
  press_button_with_text button_text
end

Then /^I read the text and proceed$/ do
	press_button_with_text 'Næste'
end

Then /^I select "(.*?)"$/ do |option|
	click_on_text option
end

Then /^I can send the results$/ do
	wait_for_button 'Ja'
	press_button_with_text 'Ja'
	wait_for_text 'Indsendt måling/svar er modtaget'
	press_button_with_text 'OK'
	wait_for_text 'Menu'
end

Then /^I can create a new messsage to "(.*?)"$/ do |recipient|
	wait_for_text 'Beskeder'
	click_on_text 'Beskeder'
	wait_for_button 'Ny besked'
	press_button_with_text 'Ny besked'
end

Then /^give it "(.*?)" as the subject$/ do |subject|
  enter_text_into_numbered_field subject, 2
end

Then /^"(.*?)" as the body$/ do |body|
  enter_text_into_numbered_field body, 3
end

Then /^I can send it$/ do
  press_button_with_text 'Send'
  wait_for_text 'Besvarelserne er nu indsendt'
  press_button_with_text 'OK'
end
