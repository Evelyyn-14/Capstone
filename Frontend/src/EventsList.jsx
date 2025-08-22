export function EventsList(params) {
  return (
    <div className="boxed">
      <h4>Events List</h4>
      <table id="event-list">
        <thead>
          <tr>
            <th>Title</th>
            <th>Date</th>
            <th>Location</th>
          </tr>
        </thead>
        <tbody>
          {params.events.map((item) => {
            return (
              <tr
                key={item.id}
                className={item.id === params.formObject.id ? "selected" : ""}
                onClick={() => params.handleListClick(item)}
              >
                <td>{item.title}</td>
                <td>{item.eventDateTime}</td>
                <td>{item.location}</td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
