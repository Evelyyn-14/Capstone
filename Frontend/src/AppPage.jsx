import React, { useState, useEffect } from 'react';
import { getAll,getAllEvents, post, put, deleteById, deleteEventById, postEvent, putEvent} from './restdb.jsx';
import './App.css';
import { useNavigate } from 'react-router-dom';
import { CustomerList } from './CustomerList.jsx';
import { EventsList } from './EventsList.jsx';
import { CustomerAddUpdateForm } from './CustomerAddUpdateForm.jsx';
import { EventAddUpdateForm } from './EventAddUpdateForm.jsx';
import { Account } from './Account.jsx';

export function App(props) {
  let blankCustomer = { "id": -1, "name": "", "email": "", "password": "" };
  let blankEvent = { "id": -1, "title": "", "eventDateTime": "", "location": "" };

  const [customers, setCustomers] = useState([]);
  const [events, setEvents] = useState([]);

  const [eventFormObject, setEventFormObject] = useState(blankEvent);
  const [formObject, setFormObject] = useState(blankCustomer);
  
  let mode = (formObject.id >= 0) ? 'Update' : 'Add';
  let modeEvent = (eventFormObject.id >= 0) ? 'Update' : 'Add';

  const navigate = useNavigate();
  if(props.username === "") {
    navigate("/login");
  }

  useEffect(() => { getCustomers() }, [formObject]);
  useEffect(() => { getEvents() }, [eventFormObject]);

  const getCustomers = function () {
    getAll(setCustomers);
  }
  const getEvents = function () {
    getAllEvents(setEvents);
  }

  const handleListClick = function (item) {
    if (formObject.id === item.id) {
      setFormObject(blankCustomer);
    } else {
      setFormObject(item);
    }
  }

  const handleEventListClick = function (item) {
    if (eventFormObject.id === item.id) {
      setEventFormObject(blankEvent);
    } else {
      setEventFormObject(item);
    }
  }

  const handleInputChange = function (event) {
    const name = event.target.name;
    const value = event.target.value;
    let newFormObject = { ...formObject }
    newFormObject[name] = value;
    setFormObject(newFormObject);
  }

  const handleEventInputChange = function (event) {
    const name = event.target.name;
    const value = event.target.value;
    let newFormObject = { ...eventFormObject }
    newFormObject[name] = value;
    setEventFormObject(newFormObject);
  }

  let onCancelClick = function () {
    setFormObject(blankCustomer);
  }

  let onEventCancelClick = function () {
    setEventFormObject(blankEvent);
  }

  let onDeleteClick = function () {
    let postopCallback = () => { setFormObject(blankCustomer); }
    if (formObject.id >= 0) {
      deleteById(formObject.id, postopCallback);
    } else {
      setFormObject(blankCustomer);
    }
  }

  let onEventDeleteClick = function () {
    let postopCallback = () => { setEventFormObject(blankEvent); }
    if (eventFormObject.id >= 0) {
      deleteEventById(eventFormObject.id, postopCallback);
    } else {
      setEventFormObject(blankEvent);
    }
  }

  let onSaveClick = function () {
    let postopCallback = () => { setFormObject(blankCustomer); }
    if (mode === 'Add') {
      post(formObject, postopCallback);
    }
    if (mode === 'Update') {
      put(formObject, postopCallback);
    }
  }

  let onEventSaveClick = function () {
    let postopCallback = () => { setEventFormObject(blankEvent); }
    if (modeEvent === 'Add') {
      postEvent(eventFormObject, postopCallback);
    }
    if (modeEvent === 'Update') {
      putEvent(eventFormObject, postopCallback);
    }

  }

  let pvars = {
    mode: mode,
    handleInputChange: handleInputChange,
    formObject: formObject,
    onDeleteClick: onDeleteClick,
    onSaveClick: onSaveClick,
    onCancelClick: onCancelClick
  }

  let epvars = {
    mode: modeEvent,
    handleInputChange: handleEventInputChange,
    formObject: eventFormObject,
    onDeleteClick: onEventDeleteClick,
    onSaveClick: onEventSaveClick,
    onCancelClick: onEventCancelClick
  }

  return ( 
    <div>
      <Account username={props.username} setUsername={props.setUsername}  />
      <CustomerList
        customers={customers}
        formObject={formObject}
        handleListClick={handleListClick}
      />
      <CustomerAddUpdateForm {...pvars} />
      <EventsList
        events={events}
        formObject={eventFormObject}
        handleListClick={handleEventListClick}
      />
      <EventAddUpdateForm {...epvars} />
    </div>
  );
}

export default App;
