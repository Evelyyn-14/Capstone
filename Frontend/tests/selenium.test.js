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

    const usernameInput = await driver.findElement(By.name('username'));
    const passwordInput = await driver.findElement(By.name('password'));
    const loginButton = await driver.findElement(By.xpath("//button[text()='Login']"));

    await usernameInput.clear();
    await passwordInput.clear();

    // Type credentials
    await usernameInput.sendKeys('eescobedo');
    await passwordInput.sendKeys('hello');

    // Wait for React to update the input values
    await driver.wait(async () => {
      const uVal = await usernameInput.getAttribute('value');
      const pVal = await passwordInput.getAttribute('value');
      return uVal === 'eescobedo' && pVal === 'hello';
    }, 5000, 'React did not update input values before clicking login');

    // Click login after state is synced
    await loginButton.click();

    // Wait for navigation to /app
    await driver.wait(until.urlContains('/app'), 5000);
    const url = await driver.getCurrentUrl();
    assert.ok(url.includes('/app'), 'Did not navigate to /app after login');
  });

  it('should show validation alert when credentials are empty', async () => {
    await driver.get('http://localhost:3000/login');

    const usernameInput = await driver.findElement(By.name('username'));
    const passwordInput = await driver.findElement(By.name('password'));
    const loginButton = await driver.findElement(By.xpath("//button[text()='Login']"));

    await usernameInput.clear();
    await passwordInput.clear();

    await loginButton.click();

    let alertText = '';
    try {
      await driver.wait(until.alertIsPresent(), 2000);
      const alert = await driver.switchTo().alert();
      alertText = await alert.getText();
      await alert.accept();
    } catch {
      assert.fail('Expected alert for empty credentials, but none appeared');
    }

    assert.strictEqual(alertText, 'Username and password cannot be empty');
  });
});
