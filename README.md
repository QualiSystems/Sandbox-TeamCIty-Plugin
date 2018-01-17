# Sandbox TeamCity Plugin

### Introduction
The **CloudShell Sandbox TeamCity Plugin** provides an easy way to consume CloudShell sandboxes through TeamCity for a variety of use cases. The plugin allows you to create build configurations that create on demand sandboxes in CloudShell based on pre-defined blueprints. 


### Requirements
This plugin requires CloudShell 8.0 or later and TeamCity 10 or later.
Depending on the plugin version, some features may require a specific version of CloudShell.

### Installation
1. Download the **CloudShell Sandbox TeamCity Plugin** from this repository's releases page.
2. Upload the *qsSandbox.zip* file to the TeamCity *data/plugins/* folder (restart is needed).

### Architecture
The **CloudShell Sandbox TeamCity Plugin** leverages CloudShell Sandbox API to perform operations in CloudShell. CloudShell Sandbox API comes out of the box with the CloudShell Suite installation and should be fully installed and configured for the plugin functionality. **Note that the TeamCity plugin only interacts with public blueprints.**

When configuring the CloudShell Sandbox API, you will need to set the API port (82 by default). To see the port, open the **CloudShell Configuration** application and click **CloudShell Sandbox API**.

### Configuration
After installing the plugin, perform the following steps:

1. Navigate to the TeamCity Administration page and open the **CloudShell Sandbox** tab.

![Alt text](pics/Screenshot_4.png?raw=true)

2. Enable the **CloudShell Sandbox** plugin.

![Alt text](pics/Screenshot_8.png?raw=true)

3. Set the CloudShell Sandbox API Host Address to the machine where CloudShell Sandbox API is installed.
Note that this may be a different machine than the Quali Server.

![Alt text](pics/Screenshot_6.png?raw=true)

4. Specify the credentials (user, password, domain) of the CloudShell user you would like to use for CloudShell operations.
We recommend creating a new CloudShell admin user for TeamCity.

5. To verify your configurations, click the **Test Connection**.
TeamCity will interact with CloudShell to validate connectivity and credentials.

![Alt text](pics/Screenshot_7.png?raw=true)

### Usage
##### CloudShell Build Steps
The plugin adds several new build steps to TeamCity to streamline interactions with CloudShell sandboxes.

**CloudShell Sandbox** is a generic build step that contains CloudShell Actions you can execute. Each action contains several inputs. Currently, the **Start sandbox** and the **Stop Sandbox** actions are provided and we plan to support others in the future.

The **Start Sandbox** action creates a new CloudShell sandbox based on the selected public blueprint and restricts interaction with the sandbox while it is running Setup. This ensures the sandbox Setup process completes successfully without any outside interference. When the sandbox is active, the sandbox’s Id and additional information become available in *%SANDBOX_ID%*. This environment variable can be used in other steps in the build configuration.

![Alt text](pics/Screenshot_1.png?raw=true)

![Alt text](pics/Screenshot_2.png?raw=true)


Note that the **Sandbox duration in minutes** field specifies the maximum duration of the sandbox. If the build does not end before the specified duration, CloudShell will tear down the sandbox.

The **Blueprint parameters** field allows you to specify user inputs, which can be used for resource selection (if the blueprint contains abstract resource requirements), as additional information to drive the provisioning, or as general information. For more information, see [CloudShell Help](http://help.quali.com/).


To end the sandbox that has been created in the build, use the **Stop Sandbox** step.
![Alt text](pics/Screenshot_3.png?raw=true)

##### CloudShell Build feature
CloudShell sandboxes can also be consumed as a TeamCity build feature. Using sandboxes as a build feature enables easy interaction with the sandbox and suites a use case where only one sandbox is needed for this build configuration.

**To use the CloudShell bulid feature:**
1. Add The **CloudShell Sandbox** build feature from the build features list.
2. Fill in all required fields.
3. Click **Save**.

For example:
![Alt text](pics/Screenshot_9.png?raw=true)

When the sandbox is active, the sandbox’s Id and additional information become available in *%SANDBOX_ID%*.

