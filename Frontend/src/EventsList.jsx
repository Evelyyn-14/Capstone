import { unregisterForEvent, registerForEvent} from './restdb.jsx';

export function EventsList({ events, formObject, handleListClick, registrations, refreshRegistrations }) {

  const isRegistered = (eventId) => {
    const reg = registrations.find(r => r.event.id === eventId);
    return reg ? reg.registration.id : null;
  }

  const handleRegisterClick = (eventId) => {
    const regId = isRegistered(eventId);
    if (regId) {
      unregisterForEvent(regId, refreshRegistrations);
    } else {
      registerForEvent(eventId, refreshRegistrations);
    }
  }

  return (
    <div className="boxed">
      <h4>Events List</h4>
      <table id="event-list">
        <thead>
          <tr>
            <th>Title</th>
            <th>Date</th>
            <th>Location</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {events.map((item) => (
            <tr
              key={item.id}
              className={item.id === formObject.id ? "selected" : ""}
              onClick={() => handleListClick(item)}
            >
              <td>{item.title}</td>
              <td>{item.eventDateTime}</td>
              <td>{item.location}</td>
              <td>
                {item.id === formObject.id && (
                  <button onClick={(e) => { e.stopPropagation(); handleRegisterClick(item.id); }}>
                    {isRegistered(item.id) ? "Unregister" : "Register"}
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
