Before do |scenario|
	@step_no = 0
end

After do |scenario|

end	

AfterStep do |scenario|
	step = scenario.raw_steps[@step_no]
	featureName = scenario.feature.title
	scenarioName = scenario.title
	stepText = "#{step.keyword}#{step.name}"
	screenshotName = "#{featureName}-#{scenarioName}-#{stepText}-#{@step_no}-#{ENV['ADB_DEVICE_ARG']}.png".gsub(" ", "_").gsub("(","_").gsub(")","_").gsub("\"", "")
  	screenshot({:name=>screenshotName})

  	@step_no = @step_no + 1
end