export function EventAddUpdateForm(params) {
    return (
      <div className="boxed">
        <div>
          <h4>{params.mode} event</h4>
        </div>
        <form>
          <table id="event-add-update">
            <tbody>
              <tr>
                <td className="label">Title:</td>
                <td>
                  <input
                    type="text"
                    name="title"
                    onChange={params.handleInputChange}
                    value={params.formObject.title || ""}
                    placeholder="Event Title"
                    required
                  />
                </td>
              </tr>
              <tr>
                <td className="label">Date/Time:</td>
                <td>
                  <input
                    type="datetime-local"
                    name="eventDateTime"
                    onChange={params.handleInputChange}
                    value={params.formObject.eventDateTime || ""}
                    placeholder="Date"
                    required
                  />
                </td>
              </tr>
              <tr>
                <td className="label">Location:</td>
                <td>
                  <input
                    type="text"
                    name="location"
                    onChange={params.handleInputChange}
                    value={params.formObject.location || ""}
                    placeholder="Location"
                    required
                  />
                </td>
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
  