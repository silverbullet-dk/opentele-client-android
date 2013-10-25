def login(username, password)
	enter_text_into_numbered_field username, 1
	enter_text_into_numbered_field password, 2
	press_button_with_text 'Login'
	wait_for_text 'Menu'
end