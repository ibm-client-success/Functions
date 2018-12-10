# Introduction

Currently, migrating data from a **DB2oC Lite Plan** to **DB2oC Flex plan** requires manual intervention. Included in this Github page, **db2migration.sh** script can automate the way the data is migrated to DB2oC Flex plan.

## Architecture

This use case will focus on running the migration using IBM Cloud Functions (FaaS) as illistruated in this diagram.

<img width="757" alt="image" src="https://media.github.ibm.com/user/20538/files/cdaff20c-f431-11e8-9994-7a3c2bc54843">

## Context

This Github page will accomplish the following goals in IBM Cloud:

1) Create an IBM Function **custom trigger** that will kickoff the action to migrate the data.

2) Create an IBM Function **action** using a supported language, in this case NodeJs, that will execute the script on the newly provisioned DB2oC instance.

### Step 1: Prerequisites

1. SoftLayer account
2. Bluemix account with access to **Functions** portal
3. Ubuntu VM 16.x (64 bit) or any other GNU/Linux flavor
4. RPM Package Manager (RPM)
5. Node-Modules (npm)
6. DB2 v11.1.3.3, Fix Pack "3" or newer
7. db2migration.sh script
8. index.js script

## Creating the enviroment

You will need to have an IBM email account as well as a SoftLayer account to order a VM (or a baremetal) machine and to download the DB2 to be installed on the targeted GNU/Linux operating system.

### Step 2:

Go to your [SoftLayer account](https://control.softlayer.com) and provision an **Ubuntu VM 16.x (64 bit)**.

### Step 3:

Once the Ubuntu VM 16.x (64 bit) has been provisioned, access the vm using ssh and login as **root**.
Make sure your GNU/Linux OS is fully patched and upgraded by executing these commands:

```$ sudo apt-get update``` and ```$ sudo apt-get upgrade```

### Step 4:

Next, install **rpm** by excuting this command:

```$ apt install rpm```

### Step 5:

Download and install the **DB2 v11.1.3.3, Fix Pack "3"** from [here](http://www-01.ibm.com/support/docview.wss?uid=swg27007053).

**Note:** 

When installing DB2, **port 50001** should be used. Also make sure the home directory is pointing to: **/opt/ibm/db2/V11.1**

Once installation of DB2 is complete, navigate to **home directory/instance** to test connectivity to your DB2 instance by executing: 

```$ su - db2inst1``` than type ```$ db2```

The output should look like this:

<img width="542" alt="image" src="https://media.github.ibm.com/user/20538/files/e61ebc64-f43c-11e8-8f5a-28484e482181">

You can now exit db2 mode by typing ```quit``` and than type ```exit``` to logout of the DB2 instance. 
You will be returned to the home directory. Type ```cd``` to exit the home directory. 

### Step 6:

- Navigate to ```/opt/ibm/db2/V11.1/instance``` folder and run ```su - db2inst1```
 
 The output should look like this:
 
 <img width="439" alt="image" src="https://media.github.ibm.com/user/20538/files/456059c0-fbc8-11e8-843f-f299cdb92203">
 
- Download the **db2migration.sh** script on your VM by using this curl command:

```curl https://github.com/ibm-client-success/Functions/tree/master/db2-migration/db2migration.sh -o db2migration.sh```

The output should look like this:

<img width="1150" alt="image" src="https://media.github.ibm.com/user/20538/files/9fbf646e-f43f-11e8-84e5-5cc88fdc9f91">

### Step 7:

- Keeping your existing terminal window, logged into your DB2 VM machine, open a new terminal window and log into your Bluemix account by entering ```bx login --sso```

- Target your account

- Type ```bx target --cf``` to target Cloud Foundry org/space interactively, or use:

```bx target --cf-api ENDPOINT -o ORG -s SPACE``` to target the org/space.

- Install the Cloud Functions plug-in

```ibmcloud plugin install cloud-functions```

- Verify that the plug-in is installed

```ibmcloud plugin list cloud-functions```

Output should look similar to this:

```
Plugin Name          Version
Cloud-Functions      1.0.16
```

- From your existing terminal, create a directory and name it **Functions**

- Download the ```index.js``` and save it into your **Functions** folder by using this curl command (make sure to complete the **host:**, **username:**, and **password:** section to reflect your enviroment):

```curl https://github.com/ibm-client-success/Functions/blob/master/db2-migration/Files/index.js -o index.js```

- From the same directory install ```npm```

```install npm```

- Create a **.zip** archive containing all files, including all dependencies

```zip -r action.zip *```

- Create the action. When you create an action from a .zip archive, you must set a value for the --kind parameter to specify your Node.js runtime version. Choose between nodejs:8 or nodejs:10

**Example:**

```ibmcloud fn action update db2/db2migrate --kind nodejs:8 action.zip```

Output should look like this:

```
$ ibmcloud fn action update db2/db2migrate --kind nodejs:8 action.zip
ok: updated action db2/db2migrate
```

### Step 8:

- Navigate to your [IBM Cloud Functions](https://console.bluemix.net/openwhisk/actions) portal

- Click on **Actions** to view the action you have created in step 7 above.

<img width="1420" alt="image" src="https://media.github.ibm.com/user/20538/files/9a411048-fbe0-11e8-87cb-220bbb122cf1">

- Click on the action you have created and click on **Invoke** to invoke the action

<img width="1428" alt="image" src="https://media.github.ibm.com/user/20538/files/bbf021f2-fbe0-11e8-8861-460388d439e8">

- Return to your DB2 ssh session and navigate to ```db2inst1/random``` folder. In the **random** folder, created by the script, type ```ls```.

- You will see the data migrated to the DB2 VM when the action was invoked in earlier step.

Output should look like this:

<img width="1123" alt="image" src="https://media.github.ibm.com/user/20538/files/c31cb534-fbe1-11e8-9af5-773143779814">

## Clean Up

- Cancel your VM Machine in SoftLayer portal

- Go to Functions portal and delete the **Action** created in step 8

<img width="1120" alt="image" src="https://media.github.ibm.com/user/20538/files/779c5bf0-fbeb-11e8-90ac-8ac3bf5e0784">

- Delete the **Functions** directory created in step 7

## Conclusion

In this tutorial we learned how to create an **Action** in IBM Function, and how to use the action to automate and to migrate data from a **DB2oC Lite Plan** to **DB2oC Flex plan** using a **.bash** script.

## References

[SoftLayer](https://control.softlayer.com)

[IBM Support - DB2 Fix Packs](http://www-01.ibm.com/support/docview.wss?uid=swg27007053)

[IBM Cloud Functions](https://console.bluemix.net/openwhisk/actions)
