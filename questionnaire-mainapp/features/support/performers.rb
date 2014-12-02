
def click_on_text(text)
  sleep(2)
  while query("* {text CONTAINS '#{text}'}").length == 0 do
    scroll_down()
  end

  return touch("* {text CONTAINS '#{text}'}")
end

def press_button_with_text(button_text)
  hide_soft_keyboard()
  return tap_when_element_exists("android.widget.Button {text CONTAINS '#{button_text}'}")
end

def wait_for_button(button_text)
  return wait_for_element_exists("android.widget.Button marked:'#{button_text}'")
end

def wait_for_text(text)
  return wait_for(:timeout => 5) { element_exists("* {text CONTAINS '#{text}'}") }
end

def enter_text_into_numbered_field(text, field_number)
  return enter_text("android.widget.EditText index:#{field_number-1}", text)
end

def clear_username
    return clear_text_in("android.widget.EditText index:0")
end

