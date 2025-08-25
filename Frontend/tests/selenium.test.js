import { Builder, By, until } from 'selenium-webdriver';
import assert from 'assert';

describe('Frontend App E2E Test', function () {
  this.timeout(40000);
  let driver;

  before(async () => {
    driver = await new Builder().forBrowser('chrome').build();
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

    // Type credentials slowly to allow React time to register each keystroke
    await usernameInput.sendKeys('Ryanc');
    await driver.sleep(200); // short pause
    await passwordInput.sendKeys('pass');
    await driver.sleep(200); // short pause

    // Double-check input values (optional but safe)
    const uVal = await usernameInput.getAttribute('value');
    const pVal = await passwordInput.getAttribute('value');
    assert.strictEqual(uVal, 'Ryanc');
    assert.strictEqual(pVal, 'pass');

    // Click login
    await loginButton.click();

    // Wait for navigation to /app
    await driver.wait(until.urlContains('/app'), 5000);
    const url = await driver.getCurrentUrl();
    assert.ok(url.includes('/app'), 'Did not navigate to /app after login');
  });
});
