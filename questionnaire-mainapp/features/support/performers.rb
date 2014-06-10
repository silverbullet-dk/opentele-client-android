
def assert_text(text) 
	return performAction('assert_text', text, true)
end

def assert_text_not_pressent(text)
	return performAction('assert_text', text, false)
end

def click_on_text(text)
	return performAction('click_on_text', text)
end

def enter_text_into_numbered_field(text, number)
	return performAction('enter_text_into_numbered_field', text, number)
end

def press_button_with_text(button_text)
	return performAction('press_button_with_text', button_text)
end

def wait_for_button(button_text)
	return performAction('wait_for_button', button_text)
end

def wait_for_text(text) 
	return performAction('wait_for_text', text)
end

def enter_text_into_numbered_field(text, field_number)
	return performAction('enter_text_into_numbered_field', text, field_number)
end

def clear_username
    return performAction('clear_numbered_field', 1)
end

