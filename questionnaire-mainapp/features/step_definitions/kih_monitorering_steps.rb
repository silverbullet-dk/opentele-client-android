#encoding:utf-8
When /^I am logged in as (.*) with password (.*)$/ do |user_name, password|
  performAction('wait', 2)
  performAction('wait_for_no_progress_bars')
  screenshot_embed Hash[:name => "I_login_as_#{user_name}_step_start"]
  login user_name, password
end

Then /^I should not see the administration options$/ do
  assert_text_not_pressent 'Vis Upload-debug-node?'
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

Then /^I read the text and proceed$/ do
	press_button_with_text 'Næste'
end

Then /^I select "(.*?)"$/ do |option|
	click_on_text option
end

Then /^I can send the results$/ do
	wait_for_button 'Ja'
	press_button_with_text 'Ja'
	wait_for_text 'Indsendelse af svar gik godt'
	press_button_with_text 'OK'
	assert_text 'Menu'
end

Then /^I can create a new messsage to "(.*?)"$/ do |recipient|
	screenshot_embed Hash[:name => 'I_can_create_new_message_step_start']
	wait_for_text 'Beskeder'
	click_on_text 'Beskeder'
	wait_for_button 'Ny besked'
	press_button_with_text 'Ny besked'
end

Then /^give it "(.*?)" as the subject$/ do |subject|
  enter_text_into_numbered_field subject, 2
  screenshot_embed Hash[:name => 'message_subject_entered']
end

Then /^"(.*?)" as the body$/ do |body|
  enter_text_into_numbered_field body, 3
  screenshot_embed Hash[:name => 'message_body_entered']
end

Then /^I can send it$/ do
  screenshot_embed Hash[:name => 'I_can_send_it_step_start']
  press_button_with_text 'Send'
  wait_for_text 'Besvarelserne er nu indsendt'
  press_button_with_text 'OK'
end
