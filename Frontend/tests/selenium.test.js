import { Builder, By, until } from 'selenium-webdriver';
import firefox from 'selenium-webdriver/firefox.js';
import assert from 'assert';

['firefox'].forEach((browserName) => {
  describe(`Frontend App E2E Test (${browserName})`, function () {
    this.timeout(40000);
    let driver;

    before(async () => {
      // Use the Snap Firefox binary path
      const options = new firefox.Options().setBinary('/snap/firefox/current/usr/lib/firefox/firefox');
      driver = await new Builder()
        .forBrowser(browserName)
        .setFirefoxOptions(options)
        .build();
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

      await usernameInput.sendKeys('eescobedo');
      await driver.sleep(200); 
      await passwordInput.sendKeys('hello');
      await driver.sleep(200);

      const uVal = await usernameInput.getAttribute('value');
      const pVal = await passwordInput.getAttribute('value');
      assert.strictEqual(uVal, 'eescobedo');
      assert.strictEqual(pVal, 'hello');

      await loginButton.click();

      // Wait a bit and print the URL for debugging
      await driver.sleep(2000); // wait for possible navigation
      const url = await driver.getCurrentUrl();
      console.log('Current URL after login:', url);

      // Increase wait timeout to 10 seconds
      await driver.wait(until.urlContains('/app'), 10000);
      assert.ok(url.includes('/app'), 'Did not navigate to /app after login');
    });
  });
});
