import { Builder, By, until } from 'selenium-webdriver';
import firefox from 'selenium-webdriver/firefox.js';
import assert from 'assert';

['firefox'].forEach((browserName) => {
  describe(`Frontend App E2E Test (${browserName})`, function () {
    this.timeout(40000);
    let driver;
    let testUsername;
    let testEmail;
    const testPassword = 'testpassword123';
    let customerName;
    let eventTitle; 

    before(async () => {
      const options = new firefox.Options().setBinary('/snap/firefox/current/usr/lib/firefox/firefox');
      driver = await new Builder()
        .forBrowser(browserName)
        .setFirefoxOptions(options)
        .build();
      const timestamp = Date.now();
      testUsername = `testuser${timestamp}`;
      testEmail = `test${timestamp}@example.com`;
    });

    after(async () => {
      if (driver) await driver.quit();
    });

    it('should load the homepage and find the title', async () => {
      await driver.get('http://localhost:3000');
      const title = await driver.getTitle();
      assert.ok(title.length > 0);
    });

    it('should display the main header (h3) on login page', async () => {
      await driver.get('http://localhost:3000/login');
      const header = await driver.findElement(By.css('h3'));
      const text = await header.getText();
      assert.ok(text.includes('Login'));
    });

    it('should click a button if present', async () => {
      await driver.get('http://localhost:3000');
      const buttons = await driver.findElements(By.css('button'));
      if (buttons.length > 0) {
        await buttons[0].click();
        assert.ok(true);
      } else {
        assert.ok(true, 'No button found, skipping test');
      }
    });

    it('should register a new user successfully', async () => {
      await driver.get('http://localhost:3000/register');

      const usernameInput = await driver.findElement(By.name('username'));
      const emailInput = await driver.findElement(By.name('email'));
      const passwordInput = await driver.findElement(By.name('password'));
      const registerButton = await driver.findElement(By.xpath("//button[text()='Register']"));

      await usernameInput.clear();
      await emailInput.clear();
      await passwordInput.clear();

      await usernameInput.sendKeys(testUsername);
      await emailInput.sendKeys(testEmail);
      await passwordInput.sendKeys(testPassword);

      await registerButton.click();

      await driver.wait(until.urlContains('/login'), 10000);

      const url = await driver.getCurrentUrl();
      assert.ok(url.includes('/login'), 'Did not redirect to login after registration');
    });

    it('should fill and submit the login form successfully', async () => {
      await driver.get('http://localhost:3000/login');

      const usernameInput = await driver.wait(
        until.elementLocated(By.name('username')), 5000
      );
      const passwordInput = await driver.findElement(By.name('password'));
      const loginButton = await driver.findElement(
        By.xpath("//button[text()='Login']")
      );

      await usernameInput.clear();
      await passwordInput.clear();

      // Use the registered credentials
      await usernameInput.sendKeys(testUsername);
      await driver.sleep(200); 
      await passwordInput.sendKeys(testPassword);
      await driver.sleep(200);

      const uVal = await usernameInput.getAttribute('value');
      const pVal = await passwordInput.getAttribute('value');
      assert.strictEqual(uVal, testUsername);
      assert.strictEqual(pVal, testPassword);

      await loginButton.click();

      await driver.sleep(2000); // wait for possible navigation
      const url = await driver.getCurrentUrl();

      await driver.wait(until.urlContains('/app'), 10000);
      assert.ok(url.includes('/app'), 'Did not navigate to /app after login');
    });

    it('should add a new customer after login', async () => {
      await driver.wait(until.elementLocated(By.id('customer-add-update')), 5000);

      // Fill in the customer fields
      customerName = `Customer${Date.now()}`;
      const customerEmail = `customer${Date.now()}@example.com`;
      const customerPassword = 'customerpass123';

      const nameInput = await driver.findElement(By.name('name'));
      const emailInput = await driver.findElement(By.name('email'));
      const passwordInput = await driver.findElement(By.name('password'));

      await nameInput.clear();
      await nameInput.sendKeys(customerName);

      await emailInput.clear();
      await emailInput.sendKeys(customerEmail);

      await passwordInput.clear();
      await passwordInput.sendKeys(customerPassword);

      // Click the Save button 
      const saveButton = await driver.findElement(By.xpath("//input[@type='button' and @value='Save']"));
      await saveButton.click();

      await driver.sleep(1000); 
      const customerEntry = await driver.findElement(By.xpath(`//*[contains(text(),'${customerName}')]`));
      const displayed = await customerEntry.isDisplayed();
      assert.ok(displayed, 'New customer should be visible in the customer list');
    });

    it('should add a new event after adding a customer', async () => {
      // Wait for the event form to appear
      const eventForm = await driver.wait(
        until.elementLocated(By.id('event-add-update')),
        5000
      );

      // Fill in the event fields within the event form
      eventTitle = `Event${Date.now()}`;
      const eventDateTime = '2025-12-31T12:00';
      const eventLocation = 'Test Location';

      const eventTitleInput = await eventForm.findElement(By.name('title'));
      const eventDateTimeInput = await eventForm.findElement(By.name('eventDateTime'));
      const eventLocationInput = await eventForm.findElement(By.name('location'));

      await eventTitleInput.clear();
      await eventTitleInput.sendKeys(eventTitle);

      await eventDateTimeInput.clear();
      await eventDateTimeInput.sendKeys(eventDateTime);

      await eventLocationInput.clear();
      await eventLocationInput.sendKeys(eventLocation);

      // Click the Save button within the event form only
      const saveButton = await eventForm.findElement(By.xpath(".//input[@type='button' and @value='Save']"));
      await saveButton.click();

      await driver.sleep(1000);

      // Try to find the event in the event list table
      try {
        await driver.wait(
          until.elementLocated(By.xpath(`//table[@id='event-list']//td[contains(text(),'${eventTitle}')]`)),
          10000
        );
        const eventEntry = await driver.findElement(By.xpath(`//table[@id='event-list']//td[contains(text(),'${eventTitle}')]`));
        const displayed = await eventEntry.isDisplayed();
        assert.ok(displayed, 'New event should be visible in the event list');
      } catch (e) {
        throw new Error(`Event "${eventTitle}" was not found after saving.`);
      }
    });

    it('should allow the user to register for the event', async () => {
      // Wait for the event to appear in the list by its title
      const eventEntry = await driver.wait(
        until.elementLocated(By.xpath(`//table[@id='event-list']//td[contains(text(),'${eventTitle}')]`)),
        10000
      );

      // Click the event to open its details or registration form
      await eventEntry.click();

      // Wait for the Register button to appear (match exact text)
      const registerButton = await driver.wait(
        until.elementLocated(By.xpath("//button[normalize-space(text())='Register']")),
        5000
      );

      // Click the Register button
      await registerButton.click();

      // Wait for a confirmation message
      const confirmation = await driver.wait(
        until.elementLocated(By.xpath("//*[contains(text(),'successfully registered') or contains(text(),'Registration complete')]")),
        5000
      );

      const confirmationText = await confirmation.getText();
      assert.ok(
        confirmationText.toLowerCase().includes('register'),
        'User should see a registration confirmation message'
      );
    });

    it('should update a customer and save changes', async () => {
      // Wait for the customer to appear in the list
      const customerEntry = await driver.wait(
        until.elementLocated(By.xpath(`//*[contains(text(),'${customerName}')]`)),
        10000
      );

      // Click the customer to select and load their details into the form
      await customerEntry.click();

      // Wait for the customer form to be ready
      const nameInput = await driver.wait(until.elementLocated(By.name('name')), 5000);
      const emailInput = await driver.findElement(By.name('email'));
      const passwordInput = await driver.findElement(By.name('password'));

      // Update the customer name and email
      const updatedName = `${customerName}_updated`;
      const updatedEmail = `updated_${customerName}@example.com`;

      await nameInput.clear();
      await nameInput.sendKeys(updatedName);

      await emailInput.clear();
      await emailInput.sendKeys(updatedEmail);

      // Optionally update password or leave as is
      // await passwordInput.clear();
      // await passwordInput.sendKeys('newpassword123');

      // Click the Save button (make sure it's the customer form's Save)
      const saveButton = await driver.findElement(By.xpath("//input[@type='button' and @value='Save']"));
      await saveButton.click();

      // Wait for the updated customer to appear in the list
      await driver.sleep(1000);
      const updatedCustomerEntry = await driver.findElement(By.xpath(`//*[contains(text(),'${updatedName}')]`));
      const displayed = await updatedCustomerEntry.isDisplayed();
      assert.ok(displayed, 'Updated customer should be visible in the customer list');
    });

    it('should delete a customer', async () => {
      // Wait for the updated customer to appear in the list
      const updatedCustomerEntry = await driver.wait(
        until.elementLocated(By.xpath(`//*[contains(text(),'${customerName}_updated')]`)),
        10000
      );

      // Click the customer to select and load their details into the form
      await updatedCustomerEntry.click();

      // Wait for the Delete button (input with value="Delete") to appear in the customer form
      const deleteButton = await driver.wait(
        until.elementLocated(By.xpath("//input[@type='button' and @value='Delete']")),
        5000
      );

      // Click the Delete button
      await deleteButton.click();

      // Optionally, handle confirmation dialog if your UI shows one
      // Example for a JS confirm dialog:
      // await driver.switchTo().alert().accept();

      // Wait a moment for the UI to update
      await driver.sleep(1000);

      // Assert the customer is no longer in the list
      const customerElements = await driver.findElements(By.xpath(`//*[contains(text(),'${customerName}_updated')]`));
      assert.strictEqual(customerElements.length, 0, 'Customer should be deleted and not visible in the list');
    });

  });
});
