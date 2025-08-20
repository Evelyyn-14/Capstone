
export function CustomerAddUpdateForm(params) {
    return (
      <div className="boxed">
        <div>
          <h4>{params.mode}</h4>
        </div>
        <form >
          <table id="customer-add-update" >
            <tbody>
              <tr>
                <td className={'label'} >Name:</td>
                <td><input
                  type="text"
                  name="name"
                  onChange={(e) => params.handleInputChange(e)}
                  value={params.formObject.name}
                  placeholder="Customer Name"
                  required /></td>
              </tr>
              <tr>
                <td className={'label'} >Email:</td>
                <td><input
                  type="email"
                  name="email"
                  onChange={(e) => params.handleInputChange(e)}
                  value={params.formObject.email}
                  placeholder="name@company.com" /></td>
              </tr>
              <tr>
                <td className={'label'} >Pass:</td>
                <td><input
                  type="text"
                  name="password"
                  onChange={(e) => params.handleInputChange(e)}
                  value={params.formObject.password}
                  placeholder="password" /></td>
              </tr>
              <tr className="button-bar">
                <td colSpan="2">
                  <input type="button" value="Delete" onClick={params.onDeleteClick} />
                  <input type="button" value="Save" onClick={params.onSaveClick} />
                  <input type="button" value="Cancel" onClick={params.onCancelClick} />
                </td>
              </tr>
            </tbody>
          </table>
        </form>
      </div>
    );
  }